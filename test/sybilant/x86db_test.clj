;;;; Copyright © 2014 Paul Stadig. All rights reserved.
;;;;
;;;; This Source Code Form is subject to the terms of the Mozilla Public
;;;; License, v. 2.0. If a copy of the MPL was not distributed with this file,
;;;; You can obtain one at http://mozilla.org/MPL/2.0/.
;;;;
;;;; This Source Code Form is "Incompatible With Secondary Licenses", as defined
;;;; by the Mozilla Public License, v. 2.0.
(ns sybilant.x86db-test
  (:require [clojure.test :refer :all]
            [sybilant.x86db :refer :all]))

(deftest test-version
  (is (= [1 11 0] (version))))

(deftest test-mnemonic-syntax
  (let [add-syntax (mnemonic-syntax "ADD" db)]
    (testing "mnemonic with explicit operands"
      (is (= {:mnemonic "ADD",
              :operands
              [{:type "b", :addressing "E", :mode "dst"}
               {:type "b", :addressing "G", :mode "src"}]}
             (nth add-syntax 0))))
    (testing "mnemonic with implicit operand"
      (is (= {:mnemonic "ADD",
              :operands
              [{:type "b", :group "gen", :nr "0", :mode "dst"}
               {:type "b", :addressing "I", :mode "src"}]}
             (nth add-syntax 4))))))
