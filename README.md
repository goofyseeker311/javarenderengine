# Java Render Engine
Java Render Engine, file structure is for Eclipse IDE for Java.

Starts in JFrame windowed mode, with a child component JPanel updating at chosen frames per second.
Shows a black grid on white canvas image behind the drawing canvas image as the transparency image regions indicator.

```
App Keys:
F5                -- Draw App
F6                -- CAD App

Draw App Keys:
ESC               -- exits the program
ALT-ENTER         -- toggles between windowed and full screen mode
ENTER             -- toggles between alpha/src composite pencil draw mode
BACKSPACE         -- erases the whole window canvas to white.
DRAG-LMB          -- black/hsb-color drag paint
SHIFT-LMB         -- rgb-color picker at cursor
ALT-LMB           -- black/hsb-color line drag paint
SHIFT-CMB         -- drag image contents on image canvas
MWHEEL            -- pencil width change (minmax)
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
F2                -- save image file dialog
F3                -- load image file dialog
SHIFT-F3          -- load image file as pencil brush dialog
```
# Installing and Running

Install Eclipse IDE for Java Developers 2023‑09 (or later) and load the repository as a java project into the IDE:
https://www.eclipse.org/downloads/packages/release/2023-09/r/eclipse-ide-java-developers

Install JAVA JDK 21 or later at, and double click on the downloaded release javarenderengine.jar to run it directly:
https://www.oracle.com/java/technologies/downloads/#java21

# Example Images

![loga7](https://github.com/goofyseeker311/javarenderengine/assets/19920254/986350a3-11a3-49ec-a325-fbac8eae7b25)
![loga7a](https://github.com/goofyseeker311/javarenderengine/assets/19920254/f75e6fbe-1dde-42ea-b4d4-dc12c2203ab4)
