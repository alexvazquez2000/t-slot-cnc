G20
G90; (Set positioning to absolute mode)
G17
M3 S7
G00 Z0.0
G00 X0.0 Y0.0
G00 X0.7500 Y0.7500
G01 X0.6250 Y0.8750 F30
G01 Z-2.7800
G00 Z0.0300
G00 Z0.0300 ; (Move to safe clearance)
G00 X0 Y0 ; (Return to home or loading position)
M30 ; (End Program)
