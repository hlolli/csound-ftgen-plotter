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
  (compile-orc "gi_ ftgen 1, 0, 1024, 10, 1")
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
        :items #js [#js {:value 5 :label "GEN05"
                         :short "Constructs functions from segments of exponential curves."}
                    #js {:value 6 :label "GEN06"
                         :short "Generates a function comprised of segments of cubic polynomials."}
                    #js {:value 7 :label "GEN07"
                         :short "Constructs functions from segments of straight lines."}
                    #js {:value 8 :label "GEN08"
                         :short "Generate a piecewise cubic spline curve."}
                    #js {:value 16 :label "GEN16"
                         :short "Creates a table from a starting value to an ending value."}
                    #js {:value 25 :label "GEN25"
                         :short "Construct functions from segments of exponential curves in breakpoint fashion."}
                    #js {:value 27 :label "GEN27"
                         :short "Construct functions from segments of straight lines in breakpoint fashion."}]}
   #js {:type  "group" :name "Numeric Value Access GEN Routines"
        :items #js [#js {:value 2 :label "GEN02"
                         :short "Transfers data from immediate pfields into a function table."}
                    #js {:value 17 :label "GEN17"
                         :short "Creates a step function from given x-y pairs."}
                    #js {:value 52 :label "GEN52"
                         :short "Creates an interleaved multichannel table from the specified source tables, in the format expected by the ftconv opcode."}]}])


(defn parse-input-params [in-str]
  (let [new-params
        (-> in-str
            (string/replace "," "")
            (string/replace "\n" " ")
            (string/replace #"[a-zA-Z]" "")
            string/trim
            (string/split #" "))
        new-params (remove empty? new-params)
        new-params (reduce #(conj
                             %1 (js/parseFloat %2))
                           [] new-params)]
    
    new-params))

(defn recalc-ftgen [local-state]
  (compile-orc (str "gi_ ftgen 1, 0, "
                    (:table-len @local-state) ", "
                    (if (.-value (:normalized @local-state))
                      "" "-")
                    (.-value (:ftgen @local-state)) ", "
                    (string/join "," (:params @local-state))))
  (perform-ksmps)
  (js/setTimeout
   #(let [table (get-table 1)]      
      (swap! local-state assoc :array table))
   100))

(defn Root []
  (let [local-state (r/atom {:p-fields     5
                             :table-len    1024
                             :params       [1]
                             :params-input "1"
                             :array        #js []
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
                            #(let [table (get-table 1)]
                               (swap! local-state assoc :array table))
                            100))
                         :noExitRuntime true
                         :print         (fn [msg] (swap! local-state assoc :error msg))
                         :printErr      (fn [msg] (js/console.log
                                                   (string/trim
                                                    (string/replace msg #"|\[m" ""))))})))
      :render
      (fn [this]
        (let []
          
          [:div
           [:h1 {:class-name "csnd-header"}
            "Csound FTGen plotter util" [:sup {:style {:font-size "12px"}} "alpha"]]
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
           [:div {:class-name "csnd-output-container"}
            [:div {:class-name "csnd-output"}
             [:p [:em "giTab"] " " [:strong "ftgen"] " 0, 0, "
              (str
               (:table-len @local-state) ", "
               (.-value (:ftgen @local-state)) ", ")
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
            [:textarea {:class     "autoExpand" :rows 3 :data-min-rows 3
                        :on-change (fn [event]
                                     (let [new-input-params
                                           (parse-input-params
                                            event.target.value)
                                           old-input-params (:params @local-state)]
                                       (swap! local-state assoc
                                              :params new-input-params
                                              :params-input event.target.value)
                                       (when (and (not= new-input-params old-input-params)
                                                  (not (empty? new-input-params)))
                                         (recalc-ftgen local-state))))
                        :value     (:params-input @local-state)}]]
           [:div {:class-name "csnd-docs"}
            [:h1 (.-label (:ftgen @local-state))]
            [:p (:short (get docs (.-value (:ftgen @local-state))))]]]))})))


(r/render
 [Root]
 (js/document.getElementById "app"))
