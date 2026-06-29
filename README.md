# t-slot-cnc

A desktop application that generates G-Code (`.nc` files) for CNC machines to cut and machine T-slot aluminum extrusions. Supports 10, 15, 30, 40, and 45 series extrusions with counterbore and drill hole configurations.

## Features

- **GUI mode** — Select an extrusion series and hole type, preview the technical drawing, and view the generated G-Code in real time.
- **Batch mode** — Generate G-Code files for all series and hole patterns at once, outputting to the `output/` directory along with a `parts.txt` parts list.
- Supports both imperial (inches) and metric (mm) units depending on the series.
- Counterbore holes use spiral circular interpolation (G02/G03) with multiple passes.
- Standardized output file naming: `output/{Series}/{hole_type}/{code}_{pattern}.nc`.

## Requirements

- Java 21
- Maven

## Build & Run

```bash
# Build
mvn clean package

# Run the GUI application
mvn spring-boot:run

# Run the batch G-Code generator
mvn compile exec:java -Dexec.mainClass="com.t_slot_cnc.Main"
```

## Create EXE

In Powershell run ```.\package.ps1```

If PowerShell blocks it due to execution policy, prefix with: ```powershell -ExecutionPolicy Bypass -File .\package.ps1```
 
When packaged, files go to C:\Users\<USER>\T-Slot CNC\output\. In Eclipse they continue writing to output/ in the project root.

## Machine Parameters (MachineService defaults)

| Parameter        | Value              |
|------------------|--------------------|
| End mill diameter | 0.25"             |
| Feed rate        | 40 IPM (inches)    |
| Spindle speed    | 7 (≈18,000 RPM)   |
| Depth per pass   | 0.02"              |
| Z-gap above material | 0.2"           |

## Extrusion Series

| Series | Width  | Units  |
|--------|--------|--------|
| 10     | 1"     | inches |
| 15     | 1.5"   | inches |
| 30     | 30 mm  | mm     |
| 40     | 40 mm  | mm     |
| 45     | 45 mm  | mm     |

## License

MIT License — Copyright 2026 Alex Vazquez
