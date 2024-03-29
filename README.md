# Java Render Engine
Java Render Engine, file structure is for Eclipse IDE for Java.

Starts in JFrame windowed mode, with a child component JPanel updating at chosen frames per second.
Shows a black grid on white canvas image behind the drawing canvas image as the transparency image regions indicator.

```
Shared Keys:
ALT-ENTER             -- toggles between windowed and full screen mode
F5                    -- Draw App
F6                    -- CAD App
F7                    -- Model App
F8                    -- Editor App
F9                    -- Game App
F12                   -- save a screenshot image file

Draw App Keys:
BACKSPACE             -- erases the whole window canvas to transparent
DRAG-LMB              -- black/hsba-color drag paint
CTRL-LMB              -- rgba-color picker at cursor
ALT-DRAG-LMB          -- black/hsba-color line drag paint
DRAG-CMB              -- drag image contents on image canvas
SHIFT                 -- speed up color change, pencil width change and rotation
MWHEEL                -- pencil width change (minmax)
TAB-MWHEEL            -- pencil rotation angle change (looping)
CTRL-MWHEEL           -- hue positive change (looping)
CTRL-ALT-MWHEEL       -- saturation positive change (minmax)
ALT-MWHEEL            -- brightness positive change (minmax)
DRAG-RMB              -- transparent drag paint eraser
ALT-DRAG-RMB          -- transparent line drag paint eraser
INSERT                -- hue positive change (looping)
DELETE                -- hue negative change (looping)
HOME                  -- saturation positive change (minmax)
END                   -- saturation negative change (minmax)
PGUP                  -- brightness positive change (minmax)
PGDOWN                -- brightness negative change (minmax)
NUMPAD+               -- pencil width larger (minmax)
NUMPAD-               -- pencil width smaller (minmax)
NUMPAD*               -- pencil type change next (looping)
NUMPAD/               -- pencil type change previous (looping)
NUMPAD9               -- pencil transparency positive (minmax)
NUMPAD8               -- pencil transparency negative (minmax)
NUMPAD6               -- pencil rotation angle change positive (looping)
NUMPAD5               -- pencil rotation angle change negative (looping)
F2                    -- save image file dialog
F3                    -- load image file dialog
SHIFT-F3              -- load image file as pencil brush dialog
CTRL-C                -- copy image to clipboard
CTRL-V                -- paste image from clipboard

CAD App Keys:
ENTER                 -- changes between polygon flat/textured/none fill modes (looping)
SHIFT-ENTER           -- changes between unlit and lit render modes (looping)
CTRL-ENTER            -- re-calculates all entity triangle surface light maps
WASD                  -- camera location change up/left/down/right (minmax)
C-SPACE               -- camera location change backward/forward (minmax)
QE                    -- camera tilt change left/right (looping)
BACKSPACE             -- removes all vector lines
SHIFT-BACKSPACE       -- reset camera to starting location
CTRL-BACKSPACE        -- reset all entity triangle surface light maps to zero
DRAG-LMB              -- material drag triangle paint, and place entity (minmax)
SHIFT-LMB             -- material picker at cursor
CTRL-DRAG-LMB         -- move line vertex
ALT-DRAG-LMB          -- vector line drag draw (in vector line mode)
PERIOD-DRAG-LMB       -- remove line vertex or triangle surface
PERIOD-DRAG-RMB       -- remove entity
DRAG-RMB              -- move/ground entity (minmax)
CTRL-DRAG-RMB         -- rotate entity (looping)
ALT-DRAG-RMB          -- scale entity (minmax)
SHIFT                 -- toggle snap to grid/vertex, drag multiple vertex, and speed movement
DRAG-CMB              -- camera location view position sideways pan (minmax)
CTRL-DRAG-CMB         -- change forward looking movement direction (looping)
MWHEEL                -- draw forward position change (minmax)
ARROW-KEYS            -- change forward looking movement direction (looping)
TAB-DRAG-CMB          -- triangle texture coordinates pan (minmax)
TAB-MWHEEL            -- triangle texture coordinates zoom
TAB-SHIFT-MWHEEL      -- triangle texture coordinates rotate
TAB-CTRL-SHIFT-MWHEEL -- triangle texture coordinates scale
TAB-CTRL-MWHEEL       -- triangle texture coordinates shear
INSERT                -- hue positive change (looping)
DELETE                -- hue negative change (looping)
HOME                  -- saturation positive change (minmax)
END                   -- saturation negative change (minmax)
PGUP                  -- brightness positive change (minmax)
PGDOWN                -- brightness negative change (minmax)
NUMPAD+               -- camera location change forward (minmax)
NUMPAD-               -- camera location change backward (minmax)
NUMPAD*               -- material emissivity positive (minmax)
NUMPAD/               -- material emissivity negative (minmax)
NUMPAD9               -- pencil transparency positive (minmax)
NUMPAD8               -- pencil transparency negative (minmax)
NUMPAD7               -- triangle single sided normal invert (looping)
NUMPAD6               -- material roughness positive (minmax)
NUMPAD5               -- material roughness negative (minmax)
NUMPAD4               -- triangle double sided zero normal (set)
NUMPAD3               -- material metallic positive (minmax)
NUMPAD2               -- material metallic negative (minmax)
NUMPAD1               -- triangle texture coordinate reset, mirror and rotate (looping)
NUMPAD0               -- run entity list updater
F2                    -- save model file dialog (all primitives)
SHIFT-F2              -- save model file dialog (surface only)
F3                    -- load model file dialog
CTRL-F3               -- load insert model file dialog
SHIFT-F3              -- load texture image file dialog
F4                    -- render and save projected view 3840x2160 image with black opaque background
CTRL-F4               -- render and save cube map view 6144x4096 image with transparent background
SHIFT-F4              -- render and save sphere map view 7680x2160 image with transparent background
CTRL-SHIFT-F4         -- render and save projected view 4K image with transparent background

Model App Keys:
BACKSPACE             -- remove loaded model and reset camera location
MOUSE-MOVE            -- change forward looking movement direction
WASD                  -- camera location change forward/left/backward/right (minmax)
C-SPACE               -- camera height change down/up (minmax)
QE                    -- camera tilt change left/right (looping)
F3                    -- load model file dialog
ENTER                 -- changes between polygon-projection/polygon-cubemap/
                          plane-projection/plane-spheremap/plane-cubemap/
                          ray-projection/ray-spheremap/ray-cubemap renderers (looping)
SHIFT-ENTER           -- changes between unlit and lit render modes (looping)

Editor App Keys:
-- none --            -- placeholder key binding

Game App Keys:
-- none --            -- placeholder key binding
```

# Installing and Running

Install Eclipse IDE for Java Developers 2023‑09 (or later) and load the repository as a java project into the IDE:
https://www.eclipse.org/downloads/packages/release/2023-09/r/eclipse-ide-java-developers

Install JAVA JDK 21 or later at, and double click on the downloaded release javarenderengine.jar to run it directly:
https://www.oracle.com/java/technologies/downloads/#java21

Alternative way of running the program is to open a console window on the javarenderengine.jar location and type command
"java -jar javarenderengine.jar", which will also show debug output text on the console window. Otherwise console debug output
can be activated in the Java Control Panel or Configure Java application -> Advanced -> Java console -> Show console and
Miscellaneous -> Place Java icon in system tray if the java icon is not already visible on your operating system tray.

# Example Images

Draw App:

![loga7a](https://github.com/goofyseeker311/javarenderengine/assets/19920254/f75e6fbe-1dde-42ea-b4d4-dc12c2203ab4)

CAD App:

![caddrawing4](https://github.com/goofyseeker311/javarenderengine/assets/19920254/19f1a3ce-23e7-43c0-8dfe-c5ff7c5e83f0)

Model App:

![modelrender](https://github.com/goofyseeker311/javarenderengine/assets/19920254/ae8251b3-419f-4578-83af-0bd4474a9231)
