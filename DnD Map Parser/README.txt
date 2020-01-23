1.  If you do not have the Java JDK installed, download and install it from here: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

2.  Generate a map using: https://donjon.bin.sh/adnd/dungeon/

    a)  Save the Player's Map and Print Map.
    
    b)  Copy the original map images to the program directory.
    
    Alternatively you could use a map created using DnD Map Maker 2D.

3.  Execute compile.bat to compile the program.

4.  If you want to reduce the black space in the map for printing purposes, execute reduceBlackSpace.bat.

5.  Execute run.bat to parse the image and create map tiles in the OUTPUT/ directory.

    Alternatively, you can execute run_no-filter.bat to parse the map without filtering for empty map tiles.

6.  Print the Player Map:

    a)  Move to the OUTPUT/<name>/map-Player directory and open the first image with IfranView, if you do not have this program you can download it from: http://www.irfanview.com/
    
    b)  Now that the first image is opened in IfranView, press 'T'. This will open the Thumbnail view.
    
    c)  Select all the map tiles in the directory and go to File / Print selected files as single images (batch print).

    d)  Choose the appropriate printer and set the Print Size to Custom with a Width of 8.00 and a Height of 10.00. And ensure Aspect ratio is turned off.

    e)  Under Position on paper, select Centered and choose inches instead of centimeters for the custom position measurements. Ensure the Width and Height are still correct.

    f)  Under Header/Footnote text add a Headnote with the value '$N' without the ''. This will print the filename of the image without the extension as a header.

    g)  Print.

7.  Print the DM Map:
   
    a)  Move to the OUTPUT/<name>map-DM directory and open the DM map in IfranView.
    
    b)  Choose an appropriate printer and set the Print Size to Best fit to page (aspect ratio)
    
    c)  Print.