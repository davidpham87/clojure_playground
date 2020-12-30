(ns snb.style)

(def colors
  {:blue "#7ca3c6"
   :red "#94282F"
   :orange "#F7A600"
   :green "#8ebc53"
   :olive "#3b5350"
   :blue-light "#cae7ea"
   :red-light "#f9c4af"
   :yellow-light "#ffe6ac"
   :green-ligth "#b6c29c"
   :blue-dark "#005e79"})

(def pallettes
  (mapv colors [:blue :red :orange :green :olive :blue-light :red-light
                :yellow-light :green-ligh :blue-dark]))


(def default-config
  {:toImageButtonOptions {:format "png" :height 560 :width 960 :scale 2}
   :displayModeBar "hover"
   :editable false
   :displaylogo false
   :responsive true
   :locale "en"})

(def default-layout
  {:showlegend true
   :autosize true
   :width nil
   :height nil
   :margin {:t 20 :b 90 :l 50 :r 0}
   :yaxis {:hoverformat ".2f"}
   :font {:family "Arial, sans-sherif"
          :size 24}
   :legend {:position :top :orientation :h}})

(def plotly-props
  {:layout default-layout
   :config default-config
   :style {:width "100%" :height "100%"}
   :useResizeHandler true})
