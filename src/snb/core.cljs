(ns snb.core
  (:require
   ["spectacle" :as sp]
   #_["react-plotly.js" :default plotly]
   [clojure.string :as str]
   [goog.object :as gobj]
   [reagent.core :as reagent]
   [reagent.dom :as dom]
   [snb.style :rename {colors snb-colors}
    :refer (plotly-props)]))

(def markdown-content "# Questions

- In our trading environment, can other parties detect and exploit our actions?
- Can we benchmark ourselves in our trading?
- How do we measure trading efficiency in central banks?

Notes:

- Why this question? Because it is the only thing I remember from my
  discussion with Mrs. Stahel.

---

# Plan

> Design is taking things apart in order to be able to put them back
> together. (...)
>
> You want to design like Bartok. (...) [You] want to communicate very well (...)
> [and] work at multiple levels.
>
> And you want to [work] like Coltrane. You want to take preparedness and
> experience, real experience with doing things.

Notes:

- Rich Hickey (Design, Composition and Performance)

- Problem: we want to be as efficient as possible for trading, but then we also
  need to prove we are efficient. We have the burden of proof.

- Participants: multiple units involved if set to production?

- Known from unknown: time efficiency, robustness of methods, methods quality,
  data availability.

- Data quality: are the data timestamp disentangled from their release?

- Alternatives: econometrics and statistical methodologies could yield as
  powerful results.

---

# Benefits, Costs and Feasibility

- Extension of knowledge and methodology, used by a growing number of market
  participants.
- High risks and expensive: the fields are still at their infancy.
- Data are probably available in the SNB/BIS databases.
- Time to first results depends on the data, scope and infrastructure.
  (Ambitious goals could be topics for PhD).

Notes:

- Biggest fixed cost: data manipulation and efficient data pipeline.

- Strategy: define a protocol, select a methodology and analyze it.

- Refocus the discussion from the methods to the principles and properties.

---

# Risks and Hedging

- Sizable technical complexity, with a simple architecture of isolated
  components.
- Possible total failure from the methodology applied on the domain side,
  hedged with reusable technical artifacts which could solve daily operational
  business problems.
- Likelihood of success: How do you define success?

Notes:

- Finance is a cruel world, only results matters.

- But should this be the case for research projects? What about negative
  results? Fixed vs growth mindset?

- Are the gain in agility, efficiency and transparency be dismissed because the
destination is not where we thought we would go?
")

(def presentation-mode (reagent/atom true))

(defn header [])
(defn footer [slide-number _]
  [:> sp/FlexBox
   {:flex-direction :column
    :align-self :flex-end
    :justify-self :flex-start
    :style {:width "100%"}}
   (when (pos? slide-number)
     [:> sp/FlexBox
      {:width 1
       :align-self :flex-end
       :style {:z-index 99999 :margin-bottom 5}}
      [:svg {:width "100%" :height 2}
       [:rect {:width "100%" :height 2}]]])
   (when (pos? slide-number)
     [:> sp/FlexBox
      {:justify-content :flex-start
       :align-self :flex-end
       :width 1
       :style {:z-index 99999}}
      [:> sp/Box [:b slide-number]]
      [:> sp/Box {:padding-left "2em" :margin-left "2em"}
       (str "14.07.2020"
            " Data Science at Central Banks"
            " | Hoang-Nam David-Olivier Pham")]])])

(def slide-style {:backgroundColor "#fff"})

(defn slide [{:keys [slide-number]} & children]
  (let [slide-hiccup [:> sp/Slide slide-style]
        content (->> (filterv some? (vec children)) (into [:<>]))
        slide-content [:> sp/FlexBox
                       {:justify-content :flex-start
                        :align-items :flex-start
                        :flex-direction :column
                        :width 1
                        :style {:height "100%"}}
                       [:div {:style {:flex 1 :width "100%"}}
                        content]
                       (footer (or slide-number nil) nil)]]
    (->> slide-content (conj slide-hiccup))))

(defn heading [s]
  [:> sp/Heading {:text-align :left
                  :font-size 40
                  :font-weight 0
                  :style {:margin-top 0 :margin-left 0}} s])

(defn template [props]
  (let [slide-number (gobj/get props "slideNumber")
        number-of-slides (gobj/get props "numberOfSlides")]
    [:<>
     [header]
     [footer slide-number number-of-slides]]))

(defn render-markdown-text [slide-number-start markdown-content]
  (for [[i content] (map-indexed vector (str/split markdown-content #"---"))
        :let [[content notes] (str/split content #"Notes:")
              [title content] (str/split (str/trim content) #"\n" 2)
              title (str/replace title #"#\s*" "")]]
    ^{:key content}
    (slide
     {:slide-number (+ i slide-number-start)}
     [heading title]
     [:> sp/Markdown content]
     (when notes
       [:> sp/Notes
        [:> sp/Markdown {:color :white} notes]]))))

(defn source-comp [sources presentation-mode]
  [:> sp/FlexBox
   {:justify-content :flex-end
    :height 1
    :align-self :flex-end
    :style {:transform (str "rotate(-90deg) translate(100%, 0)"
                            (if presentation-mode
                              "translate(0, -30px)"
                              "translate(-200px, -30px)"))
            :transform-origin "right"}}
   [:> sp/Box (str "Source" (when (> (count sources) 1) "s") ": "
                   (str/join "," sources))]])

#_(defn plot [{:keys [plotly-props title subtitle header source
                    presentation-mode]}]
  (let [style {:padding 0 :margin-top 5
               :text-align :left
               :margin-bottom 5
               :margin-left 0
               :align-item :left
               :font-size 30}]
    [:> sp/FlexBox
     {:justify-content :flex-start
      :align-items :start
      :flex-direction :column
      :width 1
      :flex 1
      :style {:width "100%" :padding-left 32}}
     [:svg {:width 400 :height 6}
      [:rect {:width "100%" :height 6}]]
     [:> sp/Text {:style (merge style {:font-weight :bold})}
      (str/upper-case title)]
     (when subtitle
       [:> sp/Text {:style (merge style {:font-size 24})}
        subtitle])
     [:svg {:width "93%" :height 3}
      [:rect {:width "100%" :height 3}]]
     (when header
       [:> sp/Text {:style (merge style {:margin-top 10 :font-size 24})}
        header])
     [:div {:style {:flex 1
                    ;; combination of flex and max-height holds the footer
                    ;; position constant
                    :max-height "30vh"
                    :width (if presentation-mode "93%" "75%")}}
      [:> plotly plotly-props]]
     [source-comp source presentation-mode]]))

(defn title-page []
  [:> sp/Slide (merge slide-style
                      {:background-color (snb-colors :blue)})
   [:> sp/Text {:font-size 60}
    [:svg {:width 200 :height 6 :style {:margin-bottom 7}}
     [:rect {:width "100%" :height 7}]]
    [:div "Data Science at Central Banks"]]
   [:> sp/Text {:font-size 32}
    [:div "Machine Learning and Reinforcement Learning for FX and Gold
    Trading"]
    [:div "David Pham"]
    [:br]
    [:div "Interview for the FX and Gold Analyst Position"]
    [:div "Zurich, 14 July 2020"]]
   [:> sp/Text [:img {:src "images/logo.svg"}]]])

(def slide-deck
  [(title-page)
   (slide {:slide-number 1}
          [heading "Why?"]
          [:> sp/FlexBox {:align-self :center}
           [:img {:src "images/programming_costs.png"}]])
   (render-markdown-text 2 markdown-content)])

(defn presentation []
  [:> sp/Deck {:theme
               {:colors {:primary "#000"
                         :secondary "#000"}
                :fonts
                {:header "Arial,sans-serif"
                 :paragraph "Arial,sans-serif"}}}
   (for [[i slide] (map-indexed vector slide-deck)]
     ^{:key i} slide)])

(defn app-element []
  (.getElementById js/document "app"))

(defn mount []
  (dom/render [presentation] (app-element)))

(defn main []
  (mount))

(defn ^:dev/after-load reload []
  (mount))

(comment
  (map + [1 2 3] [1 2 3] {:a 3 :b 3})
  )
