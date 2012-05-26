;;; SOEN6441 CourseWork
;;; 
;;; KENKEN Puzzle (http://www.kenken.com/) developed with Common LISP.
;;; 
;;; This file is created by Yuan Tao (ewan.msn@gmail.com)
;;; Licensed under GNU GPL v3

;;; $Author$
;;; $Date$
;;; $Rev$
;;; $HeadURL$
;;;

(defparameter *games* 
  (list              ; list of games
   (list             ; list of elements of a game 
    'game1           ; game name or number
    2                ; game size
    (list            ; list of cages.
     (list           ; list of elements of a cage
      '= 2           ; op & value of the cage.  
      'a1)           ; cells of the cage
     (list 
      '* 2
      'a2 'b1 'b2)
     ))))
(defparameter *cur-game* (car *games*))

; ((GAME1 2 ((= 2 A1) (* 2 A2 B1 B2))))
; the format of a game layout:
;
;        1       2               ; first line of layout
;    +-------+-------+           ; second
; A  |2=     |2*     |           ; 3th
;    |   1   |       |           ; 4th
;    +-------+       +           ; 5th
; B  |               |
;    |               |
;    +-------+-------+
(defun print-games ()
  (dolist (e *games*)
;    (setf *cur-game* e)
    (format t "Game Name: ~D~%" (car e))
    (let ((size (cadr e)))
      ; print the first line
      (format t "    ")         
      (dotimes (i size)          
        (format t "    ~D   " i))
      (format t "~%")
      
      ; second line
      (format t "    ")
      (dotimes (i size)          
        (format t "+-------"))
      (format t "+~%")

      (loop for i from 1 to size do           ; line of cells
            ; 3th line
            (format t " ~D  " (get-letter-from-num i))
            
            ; first column
            (let ((opv (get-op-and-value i 1)))
              (format t "|~D~D     " (car opv) (cadr opv)))
            
            (loop for j from 2 to size do 
                  (let ((opv (get-op-and-value i j)))
                    (if (if-2cells-connected i (- j 1) i j)
                        (format t " ~D~D     " (car opv) (cadr opv))
                      (format t "|~D~D     " (car opv) (cadr opv)))))
                  
            ; last edge of the table
            (format t "|~%")
            
            ; 4th line
            (format t "    ")
            
            ; first column
            
        ))))

(defun get-letter-from-num (num)
  (cond ((= num 1) 'A)
        ((= num 2) 'B)
        ((= num 3) 'C)
        ((= num 4) 'D)
        ((= num 5) 'E)
        ((= num 6) 'F)))

; get a list containing the operator and the value of the element (i j)
; e.g ; ((GAME1 2 ((= 2 A1) (* 2 A2 B1 B2))))
; CG-USER(51): (GET-OP-AND-VALUE 2 1)
; (* 2 A2 B1 B2)
(defun get-op-and-value (i j)
  (dolist (e (caddr *cur-game*))
    (dolist (k (cddr e))
      (if (string-equal (concatenate 'string 
                          (write-to-string (get-letter-from-num i)) 
                          (write-to-string j))
                        (write-to-string k))
          (return-from get-op-and-value e)
        nil))))

; check if two cells are connected or they are in the same cage
(defun if-2cells-connected (i1 j1 i2 j2)
  (equal (get-op-and-value i1 j1) (get-op-and-value i2 j2)))
