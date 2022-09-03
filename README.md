
# D&D Map Maker

## Design and Print Playable D&D Maps

____


![DnDMapMaker Demo Hero](https://github.com/ZGorlock/DnDMapMaker/blob/master/etc/demo/DnDMapMaker-demo-1.jpg?raw=true)

____


## Usage

To run this project you will need to have [**Java 13.0.2**](https://jdk.java.net/archive/) or higher, as well as [**Maven 3.8.6**](https://maven.apache.org/download.cgi) or higher.

\
After downloading the project files, open the `DnD Map Maker 2D` folder.

- On Windows:
  - To run the tool run: `run.bat`
  - To compile the tool run: `compile.bat`

+ On Linux:
  + To run the tool run: `run.sh`
  + To compile the tool run: `compile.sh`


 
____


## Interface

When you run the program it will look like this:

![DnDMapMaker Demo UI](https://github.com/ZGorlock/DnDMapMaker/blob/master/etc/demo/DnDMapMaker-demo-2.jpg?raw=true)


### Making your Map

- `Click` the map pieces on the right and then click the tiles on the left to place those map pieces
  - You can replace a piece with a different piece
  - You can delete a piece by replacing it with a **_Nothing_** tile
- `Right Click` and drag to move around the map
- `Scroll` to zoom
- Hold [`Alt`] and `Click` to remove a piece; equivalent to placing a **_Nothing_** tile
- Hold [`Control`] and `Click` to add a label and note for that tile for the DM
- Hold [`Shift`] and hover to visualize the boundaries of the printed **Player Map** pages


### Saving your Map

- Click **_Save_** in the lower right and give your map a name; this will save it to the `SAVE` directory
- Click **_Load_** to load a saved map, just enter the same name you entered when it was saved
- If you accidentally lose your map or close the program, look in the `AUTOSAVE` directory
  - Load the autosave with the format: '`20220127/231015`'
- Once you are done click **_Export_**; this will export it to the `OUTPUT` directory


 
____


## Output

After you export the map, a number of files will be generated in a folder with the name of your map under the `OUTPUT` directory.


### Maps

Notably, a **Player Map** _(left)_ and a **DM Map** _(right)_:

![DnDMapMaker Demo Maps](https://github.com/ZGorlock/DnDMapMaker/blob/master/etc/demo/DnDMapMaker-demo-3.jpg?raw=true)

The **Player Map** will:

- Replace **_Trapped Doors_** / **_Locked Doors_** with normal **_Doors_**
- Replace **_Secret Doors_** with **_Borders_**
- Exclude all the labels and notes for the DM


### Player Map

Under the `map-Player` folder you will see the **Player Map** divided into 8x10 sections that can be printed on 8.5"x11" paper:

![DnDMapMaker Demo Player Maps](https://github.com/ZGorlock/DnDMapMaker/blob/master/etc/demo/DnDMapMaker-demo-4.jpg?raw=true)

Map pages that are mostly empty will have horizontal lines across the page; you can choose whether you want to print these or not.

These map pages have also replaced solid black areas with a checkerboard pattern to save ink.
\
If you prefer the solid black then use the map pages under the `map-Player (original)` folder instead.

These pages are labelled at the end with coordinates _(x, y)_ indicating the position they would be laid out on the table, with _(0, 0)_ at the upper left.

I wouldn't put the entire map out at once, just bit by bit as the players move around to not expose too much.


### DM Map

Under the `map-DM` folder you will see a partitioned **DM Map**:

![DnDMapMaker Demo DM Map](https://github.com/ZGorlock/DnDMapMaker/blob/master/etc/demo/DnDMapMaker-demo-5.jpg?raw=true)

These partitions and coordinates line up with the map pages of the **Player Map**.


 
____


## Printing

The easiest way to print the maps properly is to use [**IfranView**](http://www.irfanview.com/).


### Player Map

- [ ] Open the first image with IfranView
- [ ] Press [`T`] to open the Thumbnail View
  - [ ] Select all the map pages that you want to print
- [ ] Go to **File** > **Print selected files as single images (batch print)**
  - [ ] Choose the appropriate printer
  - [ ] Select `Portrait`
  - [ ] Under **Print Size** select `Custom`
    - [ ] Set **Width**: '`8.00`'
    - [ ] Set **Height**: '`10.00`'
    - [ ] Ensure **Aspect ratio** is `Off`
  - [ ] Under **Position on paper** select `Centered`
    - [ ] Under **Units for 'custom' and position'** choose `Inches`
    - [ ] Ensure the **Width** and **Height** are still correct
  - [ ] Under **Header/Footnote text** add a **Headnote** with the value: '`$N`'  
        This will print the filename of the image without the extension as a header
- [ ] Click **Print**


### DM Map

- [ ] Open the image with IfranView
- [ ] Go to **File** > **Print**
  - [ ] Choose the appropriate printer
  - [ ] Select `Portrait` or `Landscape` depending on the map dimensions
  - [ ] Under **Print Size** select `Best fit to page (asp. ratio)`
- [ ] Click **Print**


 
____


## Using Pre-Generated Maps

You can also print pre-generated maps.

To generate a map go to [**donjon**](https://donjon.bin.sh/adnd/dungeon/) and choose a configuration that you like, then click **_Construct Dungeon_**.

At the bottom of the page click on **_Download_** and download:

- [ ] Player's Map
- [ ] Print Scale

Open the `DnD Map Parser` folder and create a new directory named `INPUT`.
\
Copy the two downloaded files to the `INPUT` directory.

Then run one or more of the following scripts:

- On Windows:
  - To convert solid black areas into a checkerboard to save ink while printing run: `reduceBlackSpace.bat`
  - To parse the map run: `run.bat`
  - To parse the map without filtering for empty map pieces run: `run_no-filter.bat`
  - To compile the tool run: `compile.bat`

+ On Linux:
  + To convert solid black areas into a checkerboard to save ink while printing run: `reduceBlackSpace.sh`
  + To parse the map run: `run.sh`
  + To parse the map without filtering for empty map pieces run: `run_no-filter.sh`
  + To compile the tool run: `compile.sh`

After you parse the map, a number of files will be generated in a folder with the name of the map under the `OUTPUT` directory.
\
See the [Output](#output) section above for more information.


 
____
