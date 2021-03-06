;;;; Copyright © Paul Stadig. All rights reserved.
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

(defn mnemonic-locs [loc]
  (filter-nodes (tag-pred :mnem) (dfs-seq loc)))

(defn mnemonics [loc]
  (map (comp first :content zip/node) (mnemonic-locs loc)))

(defn mnemonic-loc [label loc]
  (filter-nodes #(= label (first (:content %)))
                (mnemonic-locs loc)))

(defn parse-operand-children [operand node]
  (reduce (fn [operand child]
            (if (:tag child)
              (assoc operand
                (case (:tag child)
                  :a :address
                  :t :type)
                (first (:content child)))
              operand))
          operand
          (:content node)))

(defn parse-operand [node]
  (let [operand {:mode (name (:tag node))}]
    (-> operand
        (into (:attrs node))
        (parse-operand-children node))))

(defn mnemonic-syntax [label loc]
  (for [mnemonic (mnemonic-loc label loc)
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
