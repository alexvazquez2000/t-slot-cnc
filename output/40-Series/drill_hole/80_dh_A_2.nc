G21
G90; (Set positioning to absolute mode)
G17
M3 S7
G00 Z0.8
G00 X0.0 Y0.0
G00 X20.0000 Y20.0000
G01 X19.8750 Y20.1250 F30
G01 Z-74.0320
G00 Z0.7620
G00 X20.0000 Y60.0000
G01 X19.8750 Y60.1250 F30
G01 Z-74.0320
G00 Z0.7620
G00 Z0.7620 ; (Move to safe clearance)
G00 X0 Y0 ; (Return to home or loading position)
M30 ; (End Program)
