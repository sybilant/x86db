((nil
  (fill-column . 80)
  (whitespace-line-column . nil))
 (clojure-mode
  (eval ignore-errors
        (require 'whitespace)
        (whitespace-mode 0)
        (whitespace-mode 1))))
