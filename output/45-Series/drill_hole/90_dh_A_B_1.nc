G21
G90; (Set positioning to absolute mode)
G17
M3 S7
G00 Z0.8
G00 X0.0 Y0.0
G00 X22.5000 Y22.5000
G01 X22.3750 Y22.6250 F30
G01 Z-83.0320
G00 Z0.7620
G00 X67.5000 Y22.5000
G01 X67.3750 Y22.6250 F30
G01 Z-83.0320
G00 Z0.7620
G00 Z0.7620 ; (Move to safe clearance)
G00 X0 Y0 ; (Return to home or loading position)
M30 ; (End Program)
