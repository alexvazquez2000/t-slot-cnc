G21
G91; (Set positioning to relative mode)
G17
M3 S0; NX-105 ignores the spindle speed
G28 Z0; (Raises Z to home first)
G28 X0 Y0; (Go home for X and Y)
G00 X124.245Y68.406; Move towards the vice in mm
M30
