G20; (G20  inches - G21 uses mm)
G90; (Set positioning to absolute mode)
G17; (XY plane)
M3 S7; (M3 turns spindle on, S=speed being ignored)
G00 Z0.10; (gap above the material=0.1)
G00 X0.0 Y0.0; (start to user home)
G00 X0.7500 Y0.7500
G01 X0.6250 Y0.8750 F15.0
G01 Z-2.7000
G00 Z0.1000
G00 X2.2500 Y0.7500
G01 X2.1250 Y0.8750 F15.0
G01 Z-2.7000
G00 Z0.1000
G00 X3.7500 Y0.7500
G01 X3.6250 Y0.8750 F15.0
G01 Z-2.7000
G00 Z0.1000
G00 Z0.1000 ; (Move to safe clearance)
G00 X0 Y0 ; (Return to home or loading position)
M30 ; (End Program)
