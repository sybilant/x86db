;;;; Copyright © 2014 Paul Stadig. All rights reserved.
;;;;
;;;; This Source Code Form is subject to the terms of the Mozilla Public
;;;; License, v. 2.0. If a copy of the MPL was not distributed with this file,
;;;; You can obtain one at http://mozilla.org/MPL/2.0/.
;;;;
;;;; This Source Code Form is "Incompatible With Secondary Licenses", as defined
;;;; by the Mozilla Public License, v. 2.0.
(ns sybilant.x86db
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(def db (-> "x86reference.xml"
            io/resource
            io/input-stream
            xml/parse
            zip/xml-zip))

(defn dfs-seq [loc]
  (take-while (complement zip/end?) (iterate zip/next loc)))

(defn ancestor-seq [loc]
  (take-while identity (iterate zip/up loc)))

(defn filter-nodes [pred locs]
  (filter (comp pred zip/node) locs))

(defn tag-pred [name]
  (fn [node]
    (= name (:tag node))))

(defn attr-pred [name value]
  (fn [node]
    (= (get-in node [:attrs name]) value)))

(defn mnemonic [label loc]
  (filter-nodes #(and ((tag-pred :mnem) %)
                      (= label (first (:content %))))
                (dfs-seq loc)))

(defn parse-explicit-operand [operand node]
  (reduce (fn [operand child]
            (assoc operand
              (case (:tag child)
                :a :addressing
                :t :type)
              (first (:content child))))
          operand
          (:content node)))

(defn parse-implicit-operand [operand node]
  (reduce (fn [operand [name value]]
            (assoc operand name value))
          operand
          (:attrs node)))

(defn parse-operand [node]
  (let [operand {:mode (name (:tag node))}]
    (if (empty? (:attrs node))
      (parse-explicit-operand operand node)
      (parse-implicit-operand operand node))))

(defn mnemonic-syntax [label loc]
  (for [mnemonic (mnemonic label loc)
        :let [operand-nodes (->> mnemonic
                                 (iterate zip/right)
                                 rest
                                 (take-while identity))]]
    {:mnemonic label
     :operands (vec (map (comp parse-operand
                               zip/node)
                         operand-nodes))}))

(defn opcode [opcode loc]
  (filter-nodes #(and ((tag-pred :pri_opcd) %)
                      ((attr-pred :value opcode) %))
                (dfs-seq loc)))

(defn version [] [1 11 0])
