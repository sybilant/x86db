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
            [clojure.xml :refer [parse]]))

(defonce db (parse (io/input-stream (io/resource "x86reference.xml"))))

(defn version []
  [1 11 0])
