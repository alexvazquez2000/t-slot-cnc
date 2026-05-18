G21; (G20  inches - G21 uses mm)
G90; (Set positioning to absolute mode)
G17; (XY plane)
M3 S7; (M3 turns spindle on, S=speed being ignored)
G00 Z2.54; (gap above the material=2.54)
G00 X0.0 Y0.0; (start to user home)
G00 X15.0000 Y15.0000
G01 X14.8750 Y15.1250 F381.0
G01 Z-54.0000
G00 Z2.5400
G00 X45.0000 Y15.0000
G01 X44.8750 Y15.1250 F381.0
G01 Z-54.0000
G00 Z2.5400
G00 X15.0000 Y45.0000
G01 X14.8750 Y45.1250 F381.0
G01 Z-54.0000
G00 Z2.5400
G00 X45.0000 Y45.0000
G01 X44.8750 Y45.1250 F381.0
G01 Z-54.0000
G00 Z2.5400
G00 Z2.5400 ; (Move to safe clearance)
G00 X0 Y0 ; (Return to home or loading position)
M30 ; (End Program)
