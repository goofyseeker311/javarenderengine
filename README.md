# Java Render Engine
Java Render Engine, file structure is for Eclipse IDE for Java.

Starts in JFrame windowed mode, with a child component JPanel updating at chosen frames per second.
Shows a black grid on white canvas image behind the drawing canvas image as the transparency image regions indicator.

```
Shared Keys:
ALT-ENTER         -- toggles between windowed and full screen mode
F5                -- Draw App
F6                -- CAD App
F7                -- Model App
F8                -- Editor App

Draw App Keys:
ENTER             -- toggles between alpha/src composite pencil draw mode
BACKSPACE         -- erases the whole window canvas to white
DRAG-LMB          -- black/hsb-color drag paint
SHIFT-LMB         -- rgb-color picker at cursor
ALT-DRAG-LMB      -- black/hsb-color line drag paint
SHIFT-CMB         -- drag image contents on image canvas
MWHEEL            -- pencil width change (minmax)
SHIFT-MWH         -- pencil brush image rotation angle change (looping)
CTRL-MWH          -- hue positive change (looping)
CTRL-ALT-MWH      -- saturation positive change (minmax)
ALT-MWH           -- brightness positive change (minmax)
DRAG-RMB          -- transparent drag paint eraser
ALT-DRAG-RMB      -- transparent line drag paint eraser
INSERT            -- hue positive change (looping)
DELETE            -- hue negative change (looping)
HOME              -- saturation positive change (minmax)
END               -- saturation negative change (minmax)
PGUP              -- brightness positive change (minmax)
PGDOWN            -- brightness negative change (minmax)
NUMPAD+           -- pencil width larger (minmax)
NUMPAD-           -- pencil width smaller (minmax)
NUMPAD*           -- pencil type change next (looping)
NUMPAD/           -- pencil type change previous (looping)
NUMPAD9           -- pencil transparency positive (minmax)
NUMPAD8           -- pencil transparency negative (minmax)
NUMPAD6           -- pencil brush image rotation angle change positive (looping)
NUMPAD5           -- pencil brush image rotation angle change negative (looping)
F2                -- save image file dialog
F3                -- load image file dialog
SHIFT-F3          -- load image file as pencil brush dialog

CAD App Keys:
BACKSPACE         -- removes all vector lines
ALT-DRAG-LMB      -- vector line drag draw
ALT-RMB           -- remove line vertex
DRAG-LMB          -- move line vertex
SHIFT             -- toggle snap to grid
MWH               -- draw height change (minmax)
NUMPAD+           -- draw height positive (minmax)
NUMPAD-           -- draw height negative (minmax)
F2                -- save model file dialog
F3                -- load model file dialog

Model App Keys:
-- none --        -- placeholder key binding

Editor App Keys:
-- none --        -- placeholder key binding
```

# Installing and Running

Install Eclipse IDE for Java Developers 2023‑09 (or later) and load the repository as a java project into the IDE:
https://www.eclipse.org/downloads/packages/release/2023-09/r/eclipse-ide-java-developers

Install JAVA JDK 21 or later at, and double click on the downloaded release javarenderengine.jar to run it directly:
https://www.oracle.com/java/technologies/downloads/#java21

# Example Images

Draw App:

![loga7a](https://github.com/goofyseeker311/javarenderengine/assets/19920254/f75e6fbe-1dde-42ea-b4d4-dc12c2203ab4)

CAD App:

![caddrawing3](https://github.com/goofyseeker311/javarenderengine/assets/19920254/985a8822-ca04-4a61-84ad-4a9523d7e662)
