(ns csound-ftgen-plotter.core
  (:require [reagent.core :as r]
            [clojure.string :as string]
            [csound-ftgen-plotter.docs :refer [docs]]
            ["recharts" :refer [LineChart CartesianGrid
                                XAxis YAxis Line]]
            ["react-dropdown" :default Dropdown]
            ["libcsound/libcsound_browser.js" :as Libcsound]))

(def libcsound (atom nil))

(def csound-instance (atom nil))

(def csound-started? (atom false))

(defn cwrap-call [method in out]
  (((.-cwrap @libcsound) method
    in out) @csound-instance))

(defn csound-new-object []
  (reset! csound-instance
          (((.-cwrap @libcsound)
            "CsoundObj_new"
            #js ["number"] nil))))

(defn set-option [option]
  (((.-cwrap @libcsound) "CsoundObj_setOption" nil #js ["number" "string"])
   @csound-instance option))

(defn perform-ksmps []
  (((.-cwrap @libcsound)
    "CsoundObj_performKsmps" 
    #js ["number"] #js ["number"])
   @csound-instance))

#_(defn arraybuffer-to-data [arrbuf]
    (let [length  (.-length arrbuf)
          out-arr #js []]
      (loop [index 0]
        (if (< index length)
          (do
            (.push out-arr #js {:value (aget arrbuf index)
                                :index index})
            (recur (inc index)))
          out-arr))))

(defn ab->data [ab]
  (let [na #js []]
    (areduce ab i _ na
             (.push na
                    #js {:value (aget ab i) :index i}))
    na))

(defn get-table [table-num]
  (let [buf (((.-cwrap @libcsound)
              "CsoundObj_getTable"
              #js ["number"] #js ["number" "number"])
             @csound-instance table-num)
        len (((.-cwrap @libcsound) "CsoundObj_getTableLength"
              #js ["number"] #js ["number" "number"])
             @csound-instance table-num)]
    (if (= -1 len)
      {:error (str "Error: table number " table-num
                   " doesn't exist, or hasn't been compiled yet.")}
      (let [src (new js/Float64Array
                     (.-buffer (.-HEAP8 @libcsound)) buf len)
            ret (new js/Float32Array src)]
        (ab->data ret)))))

(defn compile-orc [orc]
  (((.-cwrap @libcsound)
    "CsoundObj_compileOrc"
    "number" #js ["number" "string"])
   @csound-instance orc))

(defn read-score [sco]
  (((.-cwrap @libcsound)
    "CsoundObj_readScore"
    "number" #js ["number" "string"])
   @csound-instance sco))

(defn prepareRT []
  (((.-cwrap @libcsound) "CsoundObj_prepareRT"
    nil #js ["number"]) @csound-instance))


(defn start-csound []
  (csound-new-object)
  (set-option (str "--nchnls=" 0))
  (set-option (str "--nchnls_i=" 0))
  (set-option (str "--ksmps=" 1))
  (set-option "-m0")
  (set-option "-d")
  (prepareRT)
  (compile-orc "giTab ftgen 1, 0, 1024, 10, 1")
  (compile-orc "giSrc ftgen 2, 0, 1024, 10, 1")
  (reset! csound-started? true))

(def ftgen-options #js
  [ #js {:type  "group" :name "Sine/Cosine Generators"
         :items #js [#js {:value 9 :label "GEN09"}
                     #js {:value 10 :label "GEN10"}
                     #js {:value 11 :label "GEN11"}
                     #js {:value 19 :label "GEN19"}
                     #js {:value 30 :label "GEN30"}
                     #js {:value 33 :label "GEN33"}
                     #js {:value 34 :label "GEN34"}]}
   #js {:type  "group" :name "Line/Exponential Segment Generators"
        :items #js [#js {:value 5 :label "GEN05"}
                    #js {:value 6 :label "GEN06"}
                    #js {:value 7 :label "GEN07"}
                    #js {:value 8 :label "GEN08"}
                    #js {:value 16 :label "GEN16"}
                    #js {:value 25 :label "GEN25"}
                    #js {:value 27 :label "GEN27"}]}
   #js {:type  "group" :name "Numeric Value Access GEN Routines"
        :items #js [#js {:value 2 :label "GEN02"}
                    #js {:value 17 :label "GEN17"}
                    #js {:value 52 :label "GEN52"}]}
   #js {:type  "group" :name "Window Function GEN Routines"
        :items #js [#js {:value 20 :label "GEN20"}]}
   #js {:type  "group" :name "Random Function GEN Routines"
        :items #js [#js {:value 21 :label "GEN21"}
                    #js {:value 40 :label "GEN40"}
                    #js {:value 41 :label "GEN41"}
                    #js {:value 42 :label "GEN42"}]}
   #js {:type  "group" :name "Waveshaping GEN Routines"
        :items #js [#js {:value 3 :label "GEN03"}
                    #js {:value 13 :label "GEN13"}
                    #js {:value 14 :label "GEN14"}
                    #js {:value 15 :label "GEN15"}]}
   #js {:type  "group" :name "Named GEN Routines"
        :items #js [#js {:value "tanh" :label "GENtanh"}
                    #js {:value "exp" :label "GENexp"}
                    #js {:value "sone" :label "GENsone"}
                    #js {:value "farey" :label "GENfarey"}
                    #js {:value "wave" :label "GENwave"}
                    #js {:value "padsynth" :label "GENpadsynth"}]}])

(defn parse-input-params [in-str]
  (let [new-params
        (-> in-str
            (string/replace "," " ")
            (string/replace "\n" " ")
            ;; (string/replace #"[a-zA-Z]" "")
            string/trim
            (string/split #" "))
        new-params (remove #(empty? %) new-params)
        ;; new-params (reduce #(conj
        ;;                      %1 (js/parseFloat %2))
        ;;                    [] new-params)
        ]
    
    new-params))

(defn recalc-ftgen [local-state]
  (let [gen-val     (.-value (:ftgen @local-state))
        normalized  (if (.-value (:normalized @local-state))
                      "" "-")
        global-name (if (= 1 (:table-num @local-state))
                      "giTab" "giSrc")]
    (compile-orc (str global-name
                      " ftgen " (:table-num @local-state) ", 0,"
                      (:table-len @local-state) ", "
                      (if (string? gen-val)
                        (str "\"" gen-val "\"")
                        (str normalized gen-val)) ", "
                      (string/join "," (:params @local-state)))))
  (perform-ksmps)
  (when (= 1 (:table-num @local-state))
    (js/setTimeout
     #(let [table (get-table (:table-num @local-state))]
        (swap! local-state assoc :array table))
     100)))


(defn FtgenEditor [local-state & [root-state]]
  (let [gen-val     (.-value (:ftgen @local-state))
        normalized  (if (.-value (:normalized @local-state)) "" "-")
        global-name (if (= 1 (:table-num @local-state))
                      "giTab" "giSrc")]
    [:div
     [:div {:class-name "csnd-output-container"}
      [:div {:class-name "csnd-output"}
       [:p [:em global-name] " " [:strong "ftgen"] " 0, 0, "
        (str
         (:table-len @local-state) ", "
         (if (string? gen-val)
           (str "\"" gen-val "\"")
           (str normalized gen-val)) ", ")
        (string/join ", " (:params @local-state))]]]
     [:div {:class-name "edit-area"}
      [:div
       ;; [:h1 (str (dissoc @local-state :array))]
       [:input {:value      (:table-len @local-state)
                :class-name "table-len-input"
                :on-change  (fn [event] (swap! local-state assoc
                                               :table-len event.target.value)
                              (when-not (empty? event.target.value)
                                (recalc-ftgen local-state)))}]
       [:> Dropdown {:options   #js [#js {:value true :label "Normalized"}
                                     #js {:value false :label "Non-normalized"}]
                     :value     (:normalized @local-state)
                     :on-change (fn [val]
                                  (swap! local-state assoc :normalized val)
                                  (recalc-ftgen local-state))}]
       [:> Dropdown  {:options   ftgen-options :value (:ftgen @local-state)
                      :on-change (fn [val]
                                   (swap! local-state assoc :ftgen val)
                                   (recalc-ftgen local-state))}]]
      [:textarea {:class          "autoExpand"
                  :rows           3
                  :data-min-rows  3
                  :on-change      (fn [event]
                                    (let [new-input-params
                                          (parse-input-params
                                           event.target.value)
                                          old-input-params (:params @local-state)]
                                      (swap! local-state assoc
                                             :params new-input-params
                                             :params-input event.target.value)
                                      (when (and (not= new-input-params old-input-params)
                                                 (not (empty? new-input-params)))
                                        (recalc-ftgen local-state)
                                        (when (and (= 2 (:table-num @local-state)) root-state)
                                          (recalc-ftgen root-state)))))
                  :value          (:params-input @local-state)
                  :autocomplete   "off"
                  :autocorreect   "off"
                  :autocapitalize "off"
                  :spellcheck     false}]]]))

(defn source-table-editor [cur-gen root-state]
  (let [local-state (r/atom {:table-num    2
                             :params       [1]
                             :array        #js []
                             :params-input "1"
                             :normalized   #js {:value true :label "Normalized"}
                             :ftgen        #js {:value 10 :label "GEN10"}
                             :table-len    1024})]
    (r/create-class
     {:component-did-mount
      (fn [] (get-table (:table-num @local-state)))
      :render
      (fn [this]
        [:div {:class-name "csnd-output-container"}
         [:h5 (str "Source table editor: For an argument of " cur-gen " which must point to a table.")]

         #_[:div {:id "checkbox-container"}
            [:input {:type      "checkbox" :id "checkbox-a" :name "checkbox"
                     :value     (:plotSrcTab? @root-state)
                     :on-change (fn [evt] (swap! root-state assoc :plotSrcTab? evt.target.value))}]
            [:small {:style {:top "-2px" :position "relative"}}
             "Plot the source table?"]]
         [:small "IMPORTANT: This table will be fixed to table-number 2. So refer to it with that number or its fixed name giSrc."]
         ;; [:div {:style {:height "24px"}}]
         [FtgenEditor local-state root-state]])})))

(defn Root []
  (let [local-state (r/atom {:table-num    1
                             :table-len    1024
                             :params       [1]
                             :src-params   [1]
                             :params-input "1"
                             :array        #js []
                             ;; :ftgen        #js {:value 33 :label "GEN33"}
                             ;; :plotSrcTab?  "off"
                             :ftgen        #js {:value 10 :label "GEN10"}
                             :error        nil
                             :normalized   #js {:value true :label "Normalized"}})]
    (r/create-class
     {:component-will-unmount
      (fn []
        (when @libcsound
          (((.-cwrap @libcsound) "CsoundObj_destroy" nil #js ["number"])
           @csound-instance)
          (reset! libcsound nil)))
      :component-did-mount
      (fn []
        (reset!
         libcsound
         (Libcsound #js {:postRun
                         (fn []
                           (start-csound)
                           (perform-ksmps)
                           (js/setTimeout
                            #(let [table (get-table (:table-num @local-state))]
                               (swap! local-state assoc :array table))
                            100))
                         :noExitRuntime true
                         :print         (fn [msg] (swap! local-state assoc :error msg))
                         :printErr      (fn [msg] (js/console.log
                                                   (string/trim
                                                    (string/replace msg #"|\[m" ""))))})))
      :render
      (fn [this]
        (let [normalized (if (.-value (:normalized @local-state)) "" "-")
              gen-val    (.-value (:ftgen @local-state))
              doc        (get docs (.-value (:ftgen @local-state)))]
          
          [:div
           [:div {:class-name "csnd-header-container"}
            [:h1 {:class-name "csnd-header"}
             "Csound FTGen plotter util" [:sup {:style {:font-size "12px"}} "alpha"]]]
           (when-let [error-msg (:error @local-state)]
             [:p error-msg])
           (when (< 0 (.-length (:array @local-state)))
             [:> LineChart {:width 1200 :height 600 :data (:array @local-state)
                            :style {:backgrount-color "#ffffff"}}
              [:> CartesianGrid {:stroke "#fb8200" :fill "#000000" :stroke-with 0.5
                                 :color  "purple"}]
              [:> XAxis]
              [:> YAxis]
              [:> Line {:type        "linear"
                        :dataKey     "value"
                        :stroke      "#ffeac7"
                        :dot         false
                        :width       1
                        :strokeWidth 1
                        :fillOpacity 0.3}]])
           [FtgenEditor local-state]
           (when (:srcTab? doc)
             [source-table-editor (.-label (:ftgen @local-state)) local-state])
           [:div {:class-name "csnd-docs"}
            [:h1 (.-label (:ftgen @local-state))]
            [:p (:short doc)]
            [:h2 "Syntax"]
            [:div {:class-name "syntax-container"}
             [:p "" [:strong "f"] " number time size "
              (if (string? gen-val)
                (str "\"" gen-val "\"")
                gen-val)
              " "
              [:em (string/join " " (:params (:syntax doc)))]
              (when (= :inf (:sequence (:syntax doc)))
                " ....")]]
            (into [:div
                   (if-let [size-doc (:size (:syntax doc))]
                     [:p [:sup {:style {:margin-right "6px"}} "size"] size-doc]
                     [:span])]
                  (map-indexed #(vector (if (vector? %2) :div :p)
                                        (when-not (vector? %2)
                                          [:sup {:style {:margin-right "6px"}}
                                           (str (inc %1))]) %2)
                               (:comments (:syntax doc))))
            (when-let [warnings (:warnings doc)]
              [:div [:h2 "Warnings/notes"]
               (into [:div]
                     (map #(vector :p %) warnings))])
            (when-let [examples (:examples doc)]
              [:div [:h2 "Examples"]
               (into [:div]
                     (map #(vector :p %) examples))])]
           [:footer
            [:a {:href "http://www.hlolli.com"}
             "Hlöðver Sigurðsson - 2018"]
            [:a {:href "mailto:hlolli@gmail.com"}
             "<hlolli@gmail.com>"]]]))})))


(r/render
 [Root]
 (js/document.getElementById "app"))
