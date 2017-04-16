clear
reset
set size 0.6, 0.6
set datafile separator ","
set style fill pattern 0 border
set key off
set grid
set autoscale

set xrange [0:1]
set yrange [0:1]
set xtics 0.1
set ytics 0.1

set xlabel "Prior probability"
set ylabel "Resulting posterior probability"
#use index to skip some points
plot "SB_class_prior.csv" using 1:3 every 3::1 w lp

set terminal postscript eps enhanced monochrome dashed
set output "SB_class_prior.eps"
replot