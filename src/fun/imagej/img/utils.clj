(ns fun.imagej.img.utils
  (:require [fun.imagej.img :as img]
            [fun.imagej.img.cursor :as cursor]
            [fun.imagej.ops :as ops]))

(defn interval
  "Return an Interval data structure."
  ([dimensions]
    (net.imglib2.FinalInterval. (long-array dimensions)))
  ([min max]
    (net.imglib2.FinalInterval. (long-array min) 
                                (long-array max))))

(defn tile-imgs
  "(2D only) Tile a set of images, currently assumes all are same size as the first one."
  ([imgs]
    (when-not (empty? imgs)
      (let [width (java.lang.Math/floor (/ (count imgs) (java.lang.Math/sqrt (count imgs))))
            height (java.lang.Math/ceil (/ (count imgs) width))]
        (tile-imgs imgs width height))))
  ([imgs width height]
    (let [img-width (.dimension (first imgs) 0)
          img-height (.dimension (first imgs) 1)
          tile-img ^net.imglib2.img.Img (fun.imagej.ops.create/img (interval [(* width img-width)
                                                                              (* height img-height)])
                                                                        (.firstElement (first imgs)))
          tile-ra (.randomAccess tile-img)]
      (doall (for [tx (range width)
                   ty (range height)
                   imgx (range img-width)
                   imgy (range img-height)]
               (let [img-ra ^net.imglib2.RandomAccess (.randomAccess (nth imgs (+ tx (* width ty))))]; consider getRow, putRow
                 (.setPosition tile-ra (+ imgx (* img-width tx)) 0)
                 (.setPosition tile-ra (+ imgy (* img-height ty)) 1)
                 (.setPosition img-ra imgx 0)
                 (.setPosition img-ra imgy 1)
                 (.set (.get tile-ra) (.get img-ra)))))
      tile-img)))