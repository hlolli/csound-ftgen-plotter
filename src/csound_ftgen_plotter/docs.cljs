(ns csound-ftgen-plotter.docs)

(defn spanner []
  [:span {:style {:padding-left "24px"}}])

(def docs
  {
   9  {:short  "Composite waveforms made up of weighted sums of simple sinusoids."
       :syntax {:size     "power of 2 plus 1 (2^x + 1)"
                :sequence :inf
                :params   ["partialNum" "partialStrength" "partialPhase"]
                :comments ["The partial number must be positive, but need not be a whole number, i.e., non-harmonic partials are permitted. Partials may be in any order."
                           "Strength of a partial are relative strengths, since the composite waveform may be rescaled later. Negative values are permitted and imply a 180 degree phase shift."
                           "Initial phase of the partial is expressed in degrees (0-360)"]}}
   10 {:short    "Composite waveforms made up of weighted sums of simple sinusoids."
       :syntax   {:size     "Must be either a power of 2 or power-of-2 plus 1."
                  :sequence :inf
                  :params   ["partialStrength"]
                  :comments ["Relative strengths of the fixed harmonic (integer) partial numbers 1,2,3. Partials not required should be given a strength of zero."]}
       :examples ["f 1 0 16384 10 1 - sine wave with only the fundamental frequency"
                  "f 2 0 16384 10 1 0.5 0.3 0.25 0.2 0.167 0.14 0.125 .111 - sawtooth, with a fundamental and 8 harmonics"
                  "f 3 0 16384 10 1 0 0.3 0 0.2 0 0.14 0 .111 - square wave, with a fundamental and 8 harmonics but 4 have 0 strength"
                  "f 4 0 16384 10 1 1 1 1 0.7 0.5 0.3 0.1 - pulse wave, with a fundamental and 8 harmonics"]}
   11 {:short    "Additive set of cosine partials."
       :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1"
                  :sequence :finite
                  :params   ["numberOfHarmonics" "[lowestPartial]" "[multiplier]"]
                  :comments ["Number of harmonics requested. Must be positive integer."
                             "(optional) Lowest harmonic partial present. Can be positive, zero or negative. The set of partials can begin at any partial number and proceeds upwards; if lh is negative, all partials below zero will reflect in zero to produce positive partials without phase change (since cosine is an even function), and will add constructively to any positive partials in the set. The default value is 1."
                             "(optional) Multiplier in an amplitude coefficient series. This is a power series: a given nth partial has a strength coefficient of A and (lowestPartial + harmonicNum)th partial will have a coefficient of A * multiplier * harmonicNumber, i.e. strength values trace an exponential curve. r may be positive, zero or negative, and is not restricted to integers. The default value is 1."]}
       :examples ["f 1 0 16384 11 1 1 - number of harmonics = 1"
                  "f 2 0 16384 11 10 1 .7 - number of harmonics = 10"
                  "f 3 0 16384 11 10 5 2 - number of harmonics = 10, 5th harmonic is amplified 2 times"]}
   19 {:short    "Composite waveforms made up of weighted sums of simple sinusoids."
       :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1"
                  :sequence :inf
                  :params   ["partiamNumber" "partialStrngth" "partialPhase" "partialOffset"]
                  :comments ["The partial number must be positive, but need not be a whole number, i.e., non-harmonic partials are permitted. Partials may be in any order."
                             "Strength of a partial are relative strengths, since the composite waveform may be rescaled later. Negative values are permitted and imply a 180 degree phase shift."
                             "Initial phase of the partial is expressed in degrees (0-360)"
                             "DC offset of each partials. This is applied after strength scaling, i.e. a value of 2 will lift a 2-strength sinusoid from range [-2,2] to range [0,4] (before later rescaling)."]}
       :examples ["f 2 0 1024 19 .5 .5 270 .5 - a rising sigmoid"]}
   30 {:short   "Generates harmonic partials by analyzing an existing table."
       :srcTab? true
       :syntax  {:sequence :finite
                 :params   ["sourceTable" "minHarmonic" "maxHarmonic" "[referenceSampleRate]" "[allowAmpChange]"]
                 :comments ["Source ftable"
                            "Minimum harmonic indicates the lowest harmonic number"
                            "Maximum harmonic indicates the highest harmonic number"
                            "(optional) Maxh is scaled by (sr / referenceSampleRate). The default value of referenceSampleRate is sr. If referenceSampleRate is zero or negative, it will be ignored."
                            "(optional) If allowAmpChange is non-zero, allows changing the amplitude of the lowest and highest harmonic partial depending on the fractional part of minHarmonic and maxHarmonic. For example, if maxHarmonic is 11.3 then the 12th harmonic partial is added with 0.3 amplitude. This parameter is zero by default.

"]}
       :warnings ["GEN30 does not support tables with an extended guard point (ie. table size = power of two + 1). Although such tables will work both for input and output, when reading source table(s), the guard point is ignored, and when writing the output table, guard point is simply copied from the first sample (table index = 0). The reason of this limitation is that GEN30 uses FFT, which requires power of two table size. GEN32 allows using linear interpolation for resampling and phase shifting, which makes it possible to use any table size (however, for partials calculated with FFT, the power of two limitation still exists)."]}
   33   {:short   "Generate composite waveforms by mixing simple sinusoids."
         :srcTab? true
         :syntax  {:size     "Size must be power of two and at least 4."
                   :sequence :finite
                   :params   ["sourceTable" "numberOfPartials" "scaleAmps" "frequencyMode"]
                   :comments ["Source table number must indicate a table that contains parameters of each partial in the following format: partialNum, partialStrength, partialPhase. This can be done with GEN02. The table length of the source table (not including the guard point) should be at least 3 * numberOfPartials. If the table is too short, the number of partials is reduced to (table length) / 3, rounded towards zero."
                              "Number of partials. Zero or negative values are allowed but will result in an empty table (silence). The actual number may be reduced if the source table (src) is too short, or some partials have too high frequency."
                              "Amplitude scale. 0 indicates no scaling."
                              "(optional) Frequency mode: defaults to 0. A non-zero value can be used to set frequency in Hz instead of partial numbers in the source table. The sample rate is assumed to be frequencyMode if it is positive, or -(sr * frequencyMode) if any negative value is specified."]}
         }
   34   {:short    "Generate composite waveforms by mixing simple sinusoids."
         :srcTab?  true
         :syntax   {:size     "Must be power of two or a power of two plus 1."
                    :sequence :finite
                    :params   ["sourceTable" "numberOfPartials" "scaleAmps" "frequencyMode"]
                    :comments ["Source table number must indicate a table that contains parameters of each partial in the following format: partialNum, partialStrength, partialPhase. This can be done with GEN02. The table length of the source table (not including the guard point) should be at least 3 * numberOfPartials. If the table is too short, the number of partials is reduced to (table length) / 3, rounded towards zero."
                               "Number of partials. Zero or negative values are allowed but will result in an empty table (silence). The actual number may be reduced if the source table (src) is too short, or some partials have too high frequency."
                               "Amplitude scale. 0 indicates no scaling."
                               "(optional) Frequency mode: defaults to 0. A non-zero value can be used to set frequency in Hz instead of partial numbers in the source table. The sample rate is assumed to be frequencyMode if it is positive, or -(sr * frequencyMode) if any negative value is specified."]}
         :warnings ["The difference between GEN33 and GEN34 is that GEN33 uses inverse FFT to generate output, while GEN34 is based on the algorithm used in oscils opcode. GEN33 allows integer partials only, and does not support power of two plus 1 table size, but may be significantly faster with a large number of partials. On the other hand, with GEN34, it is possible to use non-integer partial numbers and extended guard point, and this routine may be faster if there is only a small number of partials (note that GEN34 is also several times faster than GEN09, although the latter may be more accurate)."]}
   ;; Exponentials
   5    {:short    "Constructs functions from segments of exponential curves."
         :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1"
                    :sequence :inf
                    :params   ["startVal" "segmentLength1" "midVal" "segmentLength2" "endVal"]
                    :comments ["Starting values must be nonzero and must be alike midVal and endVal in sign (all positive or negative)"
                               "Segment length number 1 indicates for how many points in the table to traverse before reaching midVal"
                               "Mid values must be nonzero and must be alike startVal and endVal in sign (all positive or negative)"
                               "Segment length number 2 indicates for how many points in the table to traverse from midVal before reaching endVal"
                               "Ending values must be nonzero and must be alike startVal and midVal in sign (all positive or negative)"]}
         :examples ["f 2 0 129 5 1 100 0.0001 29 - waveform that goes over 100 points from 1 to 0.0001, stay there for 29 points"
                    "f 3 0 129 5 0.00001 87 1 22 .5 20 0.0001 - waveform that goes from 0.00001 to 1 in 87 points, then from 1 to .5 in 22 points and then from .5 to 0.0001 in 20 points"]}
   6    {:short    "Generates a function comprised of segments of cubic polynomials."
         :syntax   {:size     "Must be a power off or power-of-2 plus 1"
                    :sequence :inf
                    :params   ["startVal1" "segmentLength1" "endVal1" " [ segmentLength2 val2 segmentLengt3 val3 ]"]
                    :comments ["startVal1 is the initial value of the curve. May be either positive or negative."
                               "segmentLengthN indicates for how many points in the table to traverse before reaching valN"
                               "endVal1 is also the starting value for val2 if the table is segmented into more than 1 segments."]}
         :examples ["f 2 0 513 6 1 128 -1 128 1 64 -.5 64 .5 16 -.5 8 1 16 -.5 8 1 16 -.5 84 1 16 -.5 8 .1 16 -.1 17 0 - a not-so-smooth curve"
                    "f 3 0 513 6 0 128 0.5 128 1 128 0 129 -1 - a curve running 0 to 1 to -1, with a minimum, maximum and minimum at these values respectively. Inflexions are at .5 and 0 and are relatively smooth"]
         :warnings ["GEN06 constructs a stored function from segments of cubic polynomial functions. Segments link ordinate values in groups of 3: point of inflexion, maximum/minimum, point of inflexion. The first complete segment encompasses b, c, d and has length n2 + n3, the next encompasses d, e, f and has length n4 + n5, etc. The first segment (a, b with length n1) is partial with only one inflexion; the last segment may be partial too. Although the inflexion points b, d, f ... each figure in two segments (to the left and right), the slope of the two segments remains independent at that common point (i.e. the 1st derivative will likely be discontinuous). When a, c, e... are alternately maximum and minimum, the inflexion joins will be relatively smooth; for successive maxima or successive minima the inflexions will be comb-like."]}
   7    {:short    "Constructs functions from segments of straight lines."
         :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1"
                    :sequence :inf
                    :params   ["startingVal1" "segmentLength1" "endVal1" " [ segmentLength2 val2 segmentLengt3 val3 ] "]
                    :comments ["valN: ordinate values"
                               "segmentLengtN: Cannot be negative, but a zero is meaningful for specifying discontinuous waveforms (e.g. in the example below). The sum n1 + n2 + .... will normally equal size for fully specified functions. If the sum is smaller, the function locations not included will be set to zero; if the sum is greater, only the first size locations will be stored."]}
         :examples ["f 2 0 1024 7 0 512 1 0 -1 512 0 - sawtooth up and down, starting and ending at 0"
                    "f 3 0 1024 7 1 512 1 0 -1 512 -1 - a square from positive to negative"
                    "f 4 0 1024 7 1 1024 -1 - sawtooth down, a straight line from positive to negative"]}
   8    {:short    "Generate a piecewise cubic spline curve."
         :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                    :sequence :inf
                    :params   ["startingVal1" "segmentLength1" "endVal1" " [ segmentLength2 val2 segmentLengt3 val3 ] "]
                    :comments ["valN: ordinate values"
                               "segmentLengthN: length of each segment measured in stored values. May not be zero, but may be fractional. A particular segment may or may not actually store any values; stored values will be generated at integral points from the beginning of the function. The sum segmentLength1 + segmentLength2 + ... will normally equal size for fully specified table."]}
         :examples ["f 2 0 65 8 0 16 1 16 1 16 0 17 0 - a curve with a smooth hump in the middle, going briefly negative outside the hump then flat at its ends"
                    "f 3 0 65 8 -1 32 1 2 0 14 0 17 0 - from a negative value,a curve with a smooth hump, going negative creating a small hump then flat at its ends"]}
   16   {:short    "Creates a table from a starting value to an ending value."
         :syntax   {:size     "Must be a power of 2 or a power-of-2 plus 1."
                    :sequence :inf
                    :params   ["startingVal1" "segmentLength1" "type1" "endVal1" " [ segmentLength2 type2 val2 segmentLengt3 type3 val3 ] "]
                    :comments ["valN: ordinate values"
                               "segmentLengthN: number of segments"
                               "typeN: If 0, a straight line is produced. If non-zero, then GEN16 creates the following curve over a given segment: startingVal + (endVal - startingVal) * (1 - exp( index * type/(segmentLength - 1))) / (1 - exp(type))" ]}
         :examples ["f 2 0 1024 16 1 1024 1 0"
                    "f 3 0 1024 16 1 1024 2 0"
                    "f 4 0 1024 16 1 1024 10 0"
                    "f 5 0 1024 16 1 1024 -1 0"
                    "f 6 0 1024 16 1 1024 -2 0"
                    "f 7 0 1024 16 1 1024 -10 0"]}
   25   {:short    "Construct functions from segments of exponential curves in breakpoint fashion."
         :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                    :sequence :inf
                    :params   ["location1" "val1" "location2" "val2" " [ location3 val3 ] "]
                    :comments ["locationN Must be in increasing order. If the last value is less than size, then the rest will be set to zero. Should not be negative but can be zero."
                               "valN values must be nonzero and must be alike other values in sign (all positive or negative)"]}
         :examples ["f 2 0 1025 25 0 0.01 200 1 400 1 513 0.01 - a function which begins at 0.01, rises to 1 at the 200th table location, makes a straight line to the 400th location, and returns to 0.01 by the end of the table"]}
   27   {:short    "Construct functions from segments of straight lines in breakpoint fashion."
         :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                    :sequence :inf
                    :params   ["location1" "val1" "location2" "val2" " [ location3 val3 ] "]
                    :comments ["locationN Must be in increasing order. If the last value is less than size, then the rest will be set to zero. Should not be negative but can be zero."]}
         :examples ["f 2 0 1025 27 0 0 200 1 400 -1 513 0 - a function which begins at 0, rises to 1 at the 200th table location, falls to -1, by the 400th location, and returns to 0 by the end of the table. The interpolation is linear"]}
   ;; Numeric Value Access GEN Routines
   2    {:short    "This subroutine transfers data from immediate pfields into a function table."
         :syntax   {:size     "Must be a power of 2 or a power-of-2 plus 1 (see f statement). The maximum tablesize is 16777216 (224) points."
                    :sequence :inf
                    :params   ["value1" "value2" "valueN"]
                    :comments ["value1, value2, value3, etc. -- values to be copied directly into the table space. The number of values is limited by the compile-time variable PMAX, which controls the maximum pfields (currently 1000). The values copied may include the table guard point; any table locations not filled will contain zeros."]}
         :examples ["f 2 0 5 2 0 2 0"
                    "f 3 0 5 2 0 2 10 0"
                    "f 4 0 9 2 0 2 10 100 0"]}
   17   {:short    "Creates a step function from given x-y pairs."
         :syntax   {:size     "Must be a power of 2 or a power-of-2 plus 1 (see f statement). The maximum tablesize is 16777216 (224) points."
                    :sequence :inf
                    :params   ["location1" "val1" "location2" "val2" " [ location3 val3 ] "]
                    :comments ["locationN Must be in increasing order. If the last value is less than size, then the rest will be set to zero. Should not be negative but can be zero."
                               "valN indicates value of corresponding locationN, the value is held until next location(N+1)"]}
         :examples ["f 2 0 128 -17 0 10 32 20 64 30 96 40 - a step function with 4 equal levels, each 32 locations wide except the last which extends its value to the end of the table"]}
   52   {:short   "Creates an interleaved multichannel table from the specified source tables, in the format expected by the ftconv opcode."
         :srcTab? true
         :syntax  {:params   ["numOfChannels" "sourceTable1" "offset1" "sourceTable1Channels" " [ sourceTable2 offset2 sourceTable2Channels ]"]
                   :sequence :inf
                   :comments ["Number of channels this particular table should output (not total nchnls). When more than one channel (numOfChannels > 1) is given, source f-tables are interleaved in the newly created table."
                              "offsetN specifies an offset for the source file. If different to 0, the source file is not read from the beginning, but the offset number of values are skipped. The offset is used to determine the channel number to be read from interleaved f-tables, e.g. for channel 2, offset must be 1. It can also be used to set a read offset on the source table. This parameter gives absolute values, so if a skip of 20 sample frames for a 2 channel f-table is desired, offset must be set to 40."
                              "The sourceTableNChannels parameter is used to declare the number of channels in the source f-table. This parameter sets the skip size when reading the source f-table."]}}
   ;; Window Function GEN Routines
   20   {:short  "This subroutine generates functions of different windows. These windows are usually used for spectrum analysis or for grain envelopes."
         :syntax {:size     "Must be a power of 2 ( + 1)."
                  :params   ["windowType" "windowPeakPoint" "optinal"]
                  :sequence :finite
                  :comments [[:div {:style {:display "inline-block"}}
                              [:p [:sup {:style {:margin-right "6px"}} "1"] "Types of window to generate:"]
                              [:p [spanner] "1 = Hamming"]
                              [:p [spanner] "2 = Hanning"]
                              [:p [spanner] "3 = Bartlett ( triangle)"]
                              [:p [spanner] "4 = Blackman ( 3-term)"]
                              [:p [spanner] "5 = Blackman - Harris ( 4-term)"]
                              [:p [spanner] "6 = Gaussian"]
                              [:p [spanner] "7 = Kaiser"]
                              [:p [spanner] "8 = Rectangle"]
                              [:p [spanner] "9 = Sync"]]
                             "For non-normalized table this will be the absolute value at window peak point. If table is normalized or if non-normalized and maxPeakPoint is missing the table will be post-rescaled to a maximum value of 1."
                             "Optional argument required by the Gaussian window and the Kaiser window."]}}
   ;; Random Function GEN Routines
   21      {:short    "Generates tables of different random distributions."
            :syntax   {:size     "time and size are the usual GEN function arguments. Note that GEN21 is not self-normalizing as are most other GEN functions."
                       :params   ["type" "level" " [ arg1 [ arg2 ]] "]
                       :sequence :inf
                       :comments [[:div {:style {:display "inline-block"}}
                                   [:p [:sup {:style {:margin-right "6px"}} "1"] "Types of random distribtions:"]
                                   [:p [spanner] "1 = Uniform (positive numbers only)"]
                                   [:p [spanner] "2 = Linear (positive numbers only)"]
                                   [:p [spanner] "3 = Triangular (positive and negative numbers)"]
                                   [:p [spanner] "4 = Exponential (positive numbers only)"]
                                   [:p [spanner] "5 = Biexponential (positive and negative numbers)"]
                                   [:p [spanner] "6 = Gaussian (positive and negative numbers)"]
                                   [:p [spanner] "7 = Cauchy (positive and negative numbers)"]
                                   [:p [spanner] "8 = Positive Cauchy (positive numbers only)"]
                                   [:p [spanner] "9 = Beta (positive numbers only)"]
                                   [:p [spanner] "10 = Weibull (positive numbers only)"]
                                   [:p [spanner] "11 = Poisson (positive numbers only)"]]
                                  "level defines the amplitude"
                                  "Of all the distributin types, only 9 (Beta) and 10 (Weibull) need extra arguments. Beta needs two arguments and Weibull one."]}
            :warnings ["If type = 6, the random numbers in the ftable follow a normal distribution centered around 0.0 (mu = 0.0) with a variance (sigma) of level / 3.83. Thus more than 99.99% of the random values generated are in the range -level to +level. The default value for level is 1 (sigma = 0.261). If a mean value different of 0.0 is desired, this mean value has to be added to the generated numbers."]
            :examples ["f 1 0 32 21 1 - positive numbers only"
                       "f 2 0 32 21 6"
                       "f 3 0 32 21 6 5.745"
                       "f 4 0 32 21 9 1 1 2 - positive numbers only"
                       "f 5 0 32 21 10 1 2 - positive numbers only"]}
   40      {:short    "Generates a random distribution using a distribution histogram."
            :srcTab?  true
            :syntax   {:sequence :finite
                       :params   ["sourceTable"]
                       :comments ["The source table defines the histogram shape. Histogram shape can be generated with any other GEN routines. Since no interpolation is used when GEN40 processes the translation, it is suggested that the size of the table containing the histogram shape to be reasonably big, in order to obtain more precision (however after the processing the shaping-table can be destroyed in order to re-gain memory)."]}
            :warnings ["This subroutine is designed to be used together with cuserrnd opcode."]
            :examples ["f 1 0 16 -7 1 4 0 8 0 4 1	;distrubution using GEN07"
                       "f 2 0 16384 40 1		;GEN40 refering to table number 1 as source"]}
   41      {:short    "Generates a discrete random distribution function by giving a list of numerical pairs."
            :syntax   {:sequence :inf
                       :params   ["value1" "probability1" " [ value2 probability2 ] "]
                       :comments ["valueN: value returned if chosen"
                                  "probabilityN: Relative probability of a given valueN to be chosen. It is suggested to give it a percent value, in order to make it clearer for the user."]} 
            :warnings ["This subroutine is designed to be used together with duserrnd and urd opcodes"]
            :examples ["f 1 0 -20 -41 2 .1 8 .9"]}
   42      {:short    "Generates a random distribution function of discrete ranges of values by giving a list of groups of three numbers."
            :syntax   {:sequence :inf
                       :params   ["minVal1" "maxVal1" "probability1" " [ minVal2 maxVal2 probability2 ] "]
                       :comments ["The first number of each group is a the minimum value of the range."
                                  "The second is the maximum value"
                                  "The third is the probability of that an element belonging to that range of values can be chosen by a random algorithm. Probabilities for a range should be a fraction of 1, and the sum of the probabilities for all the ranges should total 1.0."]}
            :warnings ["This subroutine is designed to be used together with duserrnd and urd opcodes (see duserrnd for more information). Since both duserrnd and urd do not use any interpolation, it is suggested to give a size reasonably big."]}
   ;;Waveshape functions
   3       {:short    "This subroutine generates a stored function table by evaluating a polynomial in x over a fixed interval and with specified coefficients."
            :syntax   {:size     "Must be a power of 2 or a power-of-2 plus 1."
                       :sequence :inf
                       :params   ["xval1" "xval2" "c0" " [ c1 c2 c3 cN ] "]
                       :comments ["xval1 left value of the x interval over which the polynomial is defined (xval1 < xval2). These will produce the 1st stored value and the (power-of-2 plus l)th stored value respectively in the generated function table."
                                  "xval2 right value of the x interval over which the polynomial is defined (xval1 < xval2). These will produce the 1st stored value and the (power-of-2 plus l)th stored value respectively in the generated function table."
                                  "c0, c1, c2, ..., cn -- coefficients of the nth-order polynomial"]}
            :examples ["f4 0 513 3 1 1 0 1 - first-order Chebyshev: x"
                       "f6 0 513 3 -1 1 -1 0 2 - second-order Chebyshev: 2x2 - 1"
                       "f8 0 513 3 -1 1 0 -3 0 4 - third-order Chebyshev: 4x3 - 3x"
                       "f10 0 513 3 -1 10 0 -7 0 56 0 -112 0 64 - seventh-order Chebyshev: 64x7 - 112x5 + 56x3 - 7x"
                       "f12 0 513 3 -1 1 5 4 3 2 2 1 - a 4th order polynomial function over the x-interval -1 to 1"]}
   13      {:short    "Uses Chebyshev coefficients to generate stored polynomial functions which, under waveshaping, can be used to split a sinusoid into harmonic partials having a pre-definable spectrum."
            :syntax   {:size     "Must be a power of 2 or a power-of-2 plus 1."
                       :sequence :inf
                       :params   ["xinterval" "xamp" "h0" " [ h1 h2 h3 hN ] "]
                       :comments ["xinterval -- provides the left and right values [-xinterval, +xinterval] of the x interval over which the polynomial is to be drawn. These subroutines both call GEN03 to draw their functions; the p5 value here is therefor expanded to a negative-positive p5, p6 pair before GEN03 is actually called. The normal value is 1."
                                  "xamp -- amplitude scaling factor of the sinusoid input that is expected to produce the following spectrum."
                                  "h0, h1, h2, etc. -- relative strength of partials 0 (DC), 1 (fundamental), 2 ... that will result when a sinusoid of amplitude (xamp * int(size/2)/xinterval) is waveshaped using this function table. These values thus describe a frequency spectrum associated with a particular factor xamp of the input signal."]}
            :warnings ["GEN13 is the function generator normally employed in standard waveshaping. It stores a polynomial whose coefficients derive from the Chebyshev polynomials of the first kind, so that a driving sinusoid of strength xamp will exhibit the specified spectrum at output. Note that the evolution of this spectrum is generally not linear with varying xamp. However, it is bandlimited (the only partials to appear will be those specified at generation time); and the partials will tend to occur and to develop in ascending order (the lower partials dominating at low xamp, and the spectral richness increasing for higher values of xamp). A negative hn value implies a 180 degree phase shift of that partial; the requested full-amplitude spectrum will not be affected by this shift, although the evolution of several of its component partials may be. The pattern +,+,-,-,+,+,... for h0,h1,h2... will minimize the normalization problem for low xamp values, but does not necessarily provide the smoothest pattern of evolution."]}
   14      {:short    "Uses Chebyshev coefficients to generate stored polynomial functions which, under waveshaping, can be used to split a sinusoid into harmonic partials having a pre-definable spectrum."
            :syntax   {:size     "Must be either a power of 2 or a power-of-2 plus 1. The normal value is power-of-2 plus 1."
                       :sequence :inf
                       :params   ["xinterval" "xamp" "h0" " [ h1 h2 h3 hN ] "]
                       :comments ["xinterval provides the left and right values [-xinterval, +xinterval] of the x interval over which the polynomial is to be drawn. These subroutines both call GEN03 to draw their functions; the xinterval value here is therefore expanded to a negative-positive \"xinterval xamp\" pair before GEN03 is actually called. The normal value is 1."
                                  "xamp -- amplitude scaling factor of the sinusoid input that is expected to produce the following spectrum."
                                  "h0, h1, h2, etc. -- relative strength of partials 0 (DC), 1 (fundamental), 2 ... that will result when a sinusoid of amplitude ( xamp * int(size/2)/xinterval ) is waveshaped using this function table. These values thus describe a frequency spectrum associated with a particular factor xamp of the input signal."]}
            :examples ["f28 0 4097 13 1 1 1 0 .8 0 .5 0 .2 - waveshaping function: GEN13, odd harmonics"
                       "f29 0 4097 14 1 1 1 0 .8 0 .5 0 .2 - waveshaping function: GEN14, the same odd harmonics"
                       "f30 0 4097 13 1 1 0 1 0 .6 0 .4 0 .1 - waveshaping function: GEN13, even harmonics"
                       "f31 0 4097 14 1 1 0 1 0 .6 0 .4 0 .1 - waveshaping function: GEN14, the same even harmonics"]
            :warnings ["GEN13 is the function generator normally employed in standard waveshaping. It stores a polynomial whose coefficients derive from the Chebyshev polynomials of the first kind, so that a driving sinusoid of strength xamp will exhibit the specified spectrum at output. Note that the evolution of this spectrum is generally not linear with varying xamp. However, it is bandlimited (the only partials to appear will be those specified at generation time); and the partials will tend to occur and to develop in ascending order (the lower partials dominating at low xamp, and the spectral richness increasing for higher values of xamp). A negative hn value implies a 180 degree phase shift of that partial; the requested full-amplitude spectrum will not be affected by this shift, although the evolution of several of its component partials may be. The pattern +,+,-,-,+,+,... for h0,h1,h2... will minimize the normalization problem for low xamp values (see above), but does not necessarily provide the smoothest pattern of evolution."
                       "GEN14 stores a polynomial whose coefficients derive from Chebyshevs of the second kind."]}
   15      {:short    "This subroutine creates two tables of stored polynomial functions, suitable for use in phase quadrature operations."
            :syntax   {:size     "Must be either a power of 2 or a power-of-2 plus 1. The normal value is power-of-2 plus 1."
                       :sequence :inf
                       :params   ["xinterval" "xamp" "h0" "phase0" " [ h1 pahse1 h2 phase2 h3 phase3 hN phaseN ] "]
                       :comments ["xinterval provides the left and right values [-xinterval, +xinterval] of the x interval over which the polynomial is to be drawn. These subroutines both call GEN03 to draw their functions; the xinterval value here is therefore expanded to a negative-positive \"xinterval xamp\" pair before GEN03 is actually called. The normal value is 1."
                                  "xamp -- amplitude scaling factor of the sinusoid input that is expected to produce the following spectrum."
                                  "h0, h1, h2, etc. -- relative strength of partials 0 (DC), 1 (fundamental), 2 ... that will result when a sinusoid of amplitude (xamp * int(size/2)/xinterval) is waveshaped using this function table. These values thus describe a frequency spectrum associated with a particular factor xamp of the input signal."
                                  "phase0, phase1, ... -- phase in degrees of desired harmonics h0, h1, ... when the two functions of GEN15 are used with phase quadrature."]}
            :warnings ["GEN15 creates two tables of equal size, labeled f # and f # + 1. Table # will contain a Chebyshev function of the first kind, drawn using GEN13 with partial strengths h0cos(phs0), h1cos(phs1), ... Table #+1 will contain a Chebyshev function of the 2nd kind by calling GEN14 with partials h1sin(phs1), h2sin(phs2),... (note the harmonic displacement). The two tables can be used in conjunction in a waveshaping network that exploits phase quadrature."
                       "Before version 5.16 there was a bug (pointed out by Menno Knevel and fixed by François Pinot) on the number of pfields transmitted to gen13 and gen14 by gen15. The consequence is that all the csd, or orc and sco files that used gen15 before this bug was fixed, are likely to sound different now."]
            :examples ["f 33 0 8193 -15 1 1 1 0 1 180 .8 45 .6 270 .5 90 .4 225 .2 135 .1 315"
                       "f 33 0 8193 -15 1 1 1 0 1 0 1 180 1 180 1 0 1 0 1 180 1 180 1 0 1 0 1 180 1 180"
                       "f 33 0 8193 -15 1 1 1 0 1 0 .9 180 .5 270 .75 90 .4 45 .2 225 .1 0"
                       "f 33 0 8193 -15 1 1 1 0 1 0 .5 0 .9 0 .3 0 .75 0 .2 180 .6 180 .15 180 .5 180 .1 180"
                       "f 33 0 8193 -15 1 1 1 180 1 180 .5 180 .9 180 .3 180 .75 180 .2 0 .6 0 .15 0 .5 0 .1 0"]}
   "tanh"  {:short    "Fills a table from a hyperbolic tangent formula."
            :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                       :sequence :finite
                       :params   ["start" "end" "rescale"]
                       :comments ["first value to be stored; the GEN draws a curve that goes from start to end: tanh(start) .... tanh(end). The points stored are uniformly spaced between these to the table size"
                                  "second value to be stored"
                                  "rescale -- if not zero the table is not rescaled"]}
            :examples ["f 2 0 8192 \"tanh\" -100 100 0 - lots of distortion"
                       "f 3 0 8192 \"tanh\" -10 10 0 - less distortion than f2"
                       "f 4 0 8192 \"tanh\" -10 15 0"]}
   "exp"   {:short    "Creates an ftable with values of the exp function."
            :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                       :sequence :finite
                       :params   ["start" "end" "rescale"]
                       :comments ["First value to be stored; the GEN draws a curve that goes from start to end: exp(start) .... exp(end). The points stored are uniformly spaced between these to the table size"
                                  "Last value to be stored."
                                  "rescale -- if not zero the table is not rescaled"]}
            :examples ["f 2 0 8192 \"exp\" 0 15 0"
                       "f 3 0 8192 \"exp\" 0 3 0"]}
   "sone"  {:short    "Creates an ftable with values of the sone function for equal power."
            :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                       :sequence :finite
                       :params   ["start" "end" "equalpoint" "rescale"]
                       :comments ["First value to be stored. The points stored are uniformly spaced between these to the table size."
                                  "Last value to be stored."
                                  "equalpoint -- the point on the curve when the input and output values are equal."
                                  "rescale -- if not zero the table is not rescaled"]}
            :warnings ["The table is filled with the function x*POWER(x/eqlp, FL(33.0)/FL(78.0)) for x between the start and end points. This is the Sone loudness curve."]
            :examples ["f 2 0 16385 \"sone\" 0 32000 32000 0"]}
   "farey" {:short    "\"farey\" — Fills a table with the Farey Sequence Fn of the integer n."
            :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                       :sequence :finite
                       :params   ["fareynum" "mode"]
                       :comments [[:p [:sup {:style {:margin-right "6px"}} "1"]
                                   "fareynum -- the integer n for generating Farey Sequence F" [:sub "n"]]
                                  [:div {:style {:display "inline-block" :margin-top "-10px"}}
                                   [:p [:sup {:style {:margin-right "6px"}} "2"] "mode -- integer to trigger a specific output to be written into the table:"]
                                   [:p [spanner] "0 -- outputs floating point numbers representing the elements of F" [:sub "n"] "."]
                                   [:p [spanner] "1 -- outputs delta values of successive elements of F" [:sub "n"]
                                    ", useful for generating note durations for example."]
                                   [:p [spanner] "2 -- outputs only the denominators of the integer ratios, useful for indexing other tables or instruments for example."]
                                   [:p [spanner] "3 -- same as mode 2 but with normalised output."]
                                   [:p [spanner] "4 -- same as mode 0 but with 1 added to each number, useful for generating tables for tuning opcodes, for example cps2pch."]]]}
            :examples ["gidelta ftgen 100,0,-18,\"farey\",7,1 - delta values of Farey Sequence 7"
                       "gimult ftgen 101,0,-18,\"farey\",7,2 - generate the denominators of fractions of F_7"]}

   "wave"     {:short    "Creates a compactly supported wavelet, scaling function or wavelet packet. The output function is obtained by deconvolution of corresponding mirror filter impulse response. This procedure is applied in an iterative fashion."
               :srcTab?  true
               :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                          :sequence :finite
                          :params   ["fnsf" "seq" "rescale"]
                          :comments ["fnsf -- pre-existing table with scaling function coefficients."
                                     "seq -- non-negative integer number which corresponds to sequence of low-pass and high-pass mirror filters during deconvolution procedure."
                                     "rescale -- if not zero the table is not rescaled"]
                          }
               :examples ["f 1 0 4 -2 -0.1294095226 0.2241438680 0.8365163037 0.4829629131 -- source Table for GENwave"
                          "f 3 0 16384 \"wave\" 1 14 0 -- GENwave useing table1 as source"]}
   "padsynth" {:short    "Creates an ftable for use in an oscillator with values creared by the padsynth algorithm."
               :syntax   {:size     "Must be a power of 2 or power-of-2 plus 1."
                          :sequence :inf
                          :params   ["fundamentalFreq" "partialBW" "partial_Scale" "harmonicStretch" "harmonicAmp1"
                                     " [ harmonicAmp2 harmonicAmp3 harmonicAmpN ]" ]
                          :comments ["fundamentalFrq -- fundamental frequency for the generated table."
                                     "partialBW -- bandwidth of each partial in cents."
                                     "partialScale -- ration that bandwidth inccreases with harmonic number."
                                     "harmonicStretchScale -- ratio of stretch of thre overtoes."
                                     "harmonicAmp1, harmonicAmp2, ... -- relative amplitudes of the partials"]}
               :examples ["gi_padsynth_1 ftgenonce 0, 0, gispec_len, \"padsynth\", ibasehz, p6, 0.0,  1, 1,  1.0, 1"]}})
