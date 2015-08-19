(ns funimage.imp.calculator
(:require [clojure.string :as string]
            [clojure.java.io :as io])
  (:import [java.io File]
           [ij IJ ImagePlus ImageStack]
           [loci.plugins BF]
           [ij.io FileSaver]
           [javax.media.j3d Transform3D]
           [javax.vecmath Vector3f Point3f Quat4f]
           [ij.gui NewImage Toolbar Roi]
           [ij.process ImageProcessor ByteProcessor ImageStatistics]
           [ij.measure Calibration]
           [ij.plugin ImageCalculator]
           
           [ij.io Opener]
           ;[io.scif.img ImgOpener]
           
           [java.util.concurrent Executors]
           [java.awt Canvas Graphics]
           [javax.swing JFrame JMenu JMenuBar JMenuItem]
           
           [net.imglib2.img ImagePlusAdapter Img]
           [net.imglib2.img.display.imagej ImageJFunctions]
           [net.imglib2.type NativeType]
           [net.imglib2.type.numeric NumericType ARGBType]
           [net.imglib2.type.numeric.real FloatType]
           [net.imglib2.algorithm.gauss3 Gauss3]
           [net.imglib2.algorithm.dog DifferenceOfGaussian]
           [net.imglib2.view Views IntervalView]
           [net.imglib2 Cursor RandomAccess]))

(defn image-calculator-fn
  "Image calculator function from string."
  [function-name create? stack? float?]
  #(let [ic (ImageCalculator.)
         result (.run ic (string/join " " (filter identity [function-name (when create? "create") (when float? "32-bit") (when stack? "stack")])) %1 %2)]     
     (if create?
       result
       %1)))

(def imp-add "Add 2 image pluses. img1 = img1+img2" (image-calculator-fn "Add" false true false))
(def imp-subtract "Subtract 2 image pluses. img1 = img1-img2" (image-calculator-fn "Subtract" false true false))
(def imp-multiply "Multiply 2 image pluses. img1 = img1*img2" (image-calculator-fn "Multiply" false true false))
(def imp-divide "Divide 2 image pluses. img1 = img1/img2" (image-calculator-fn "Divide" false true false))
(def imp-and "AND 2 image pluses. img1= img1 AND img2" (image-calculator-fn "AND" false true false))
(def imp-or "OR 2 image pluses. img1 = img1 OR img2" (image-calculator-fn "OR" false true false))
(def imp-xor "XOR 2 image pluses. img1 = img1 XOR img2" (image-calculator-fn "XOR" false true false))
(def imp-min "Min 2 image pluses. img1 = min(img1,img2)" (image-calculator-fn "Min" false true false))
(def imp-max "Max 2 image pluses. img1 = max(img1,img2)" (image-calculator-fn "Max" false true false))
(def imp-average "Average 2 image pluses. img1 = (img1+img2)/2" (image-calculator-fn "Average" false true false))
(def imp-difference "Difference 2 image pluses. img1 = ¦img1-img2¦" (image-calculator-fn "Difference" false true false))
(def imp-copy "Copy 2 image pluses. img1 = img2" (image-calculator-fn "Copy" false true false))
(def imp-copy-create "Copy 2 image pluses. img1 = img2" (image-calculator-fn "Copy" true true false))
(defn imp-scale
  "Scale an imp by a coefficient."
  [^ij.ImagePlus imp scalar]
  (let [^ij.ImageStack stack (.getImageStack imp)]
    (doall (for [x (range (.getWidth imp))
                 y (range (.getHeight imp))
                 z (range (.getSize stack))]
             (.setVoxel stack x y z
               (* (.getVoxel stack x y z)
                  scalar)))))
  imp)
;(def imp-transparent-zero "pixels of img2 are transparent" (image-calculator-fn "Transparent Zero" false true false))

