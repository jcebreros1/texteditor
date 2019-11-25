package editor;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.*;


public class Editor extends Application {
    //Group root;
    ArrayList <Group> root;
    private int indexOfRoot;
    //Cursor pos;
    private final Rectangle cursor;

    private int linerow = 0;
    private int linecol = 0;
    private int curPosX = 5;
    private int curPosY = 0;
    private static String outFile;
    private static String inFile;

    /** The Text to display on the screen. */
    private int fontSize = 12;
    private String fontName = "Verdana";
    //private LinkedListDeque buffer = new LinkedListDeque(fontName,fontSize);
    private ArrayList<LinkedListDeque> arrayList= new ArrayList<LinkedListDeque>();
    private int arrayListSize = 1;
    private static int WINDOW_WIDTH = 500;
    private static int WINDOW_HEIGHT = 500;

    public Editor() {
        // Create a rectangle to surround the text that gets displayed.  Initialize it with a size
        // of 0, since there isn't any text yet.
        // Create a Node that will be the parent of all things displayed on the screen.
        //root = new Group();
        root = new ArrayList<Group>();
        root.add(new Group());
        indexOfRoot = 0;
        //pos = new Cursor(buffer.getCurrentPos().getX(),buffer.getCurrentPos().getY());
        cursor = new Rectangle();
        arrayList.add(new LinkedListDeque());
    }

    /** An EventHandler to handle keys that get pressed. */
    private class KeyEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.isShortcutDown()){
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.P){
                    System.out.println(curPosX + "," + curPosY);
                }
                if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                    fontSize+=4;
                    render();
                    updateCursor();
                }
                if (code == KeyCode.MINUS) {
                    if (fontSize == 4)
                        return;
                    fontSize-=4;
                    render();
                }
                else if(code == KeyCode.S){

                }
            }
            else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String s = keyEvent.getCharacter();
                if (!s.equals("") && s.charAt(0) == '\r'){
                    Text dummyText = new Text("A");
                    dummyText.setFont(Font.font(fontName, fontSize));
                    curPosY += Math.round(dummyText.getLayoutBounds().getHeight());
                    curPosX = 5;
                    arrayList.add(linerow+1,new LinkedListDeque());
                    LinkedListDeque previousRow = arrayList.get(linerow);
                    linerow++;
                    arrayListSize++;
                    int numberOfItemsInFrontOfCursor = previousRow.size() - 1 - previousRow.getIndexOfLetterCursorIsOn();
                    if (previousRow.getIndexOfLetterCursorIsOn() == previousRow.size()-1){
                        updateCursor();
                    }
                    else{
                        for (int i = 0; i < numberOfItemsInFrontOfCursor;i++){
//                            if (i == previousRow.size())
//                                return;
                            previousRow.increaseLetterCursorIsCurrentlyOn();
                            arrayList.get(linerow).add(previousRow.delete());
                        }
                    }
                    arrayList.get(linerow).setWhatCursorPointsToFirst();
                    linecol = 0;
                    render();
                }
                else if (s.length() > 0) {
                    // Ignore control keys, which have non-zero length, as well as the backspace
                    // key, which is represented as a character of value = 8 on Windows.
                    if (s.charAt(0) == ' '){
                        arrayList.get(linerow).setSingleLineToFalse();
                    }
                    Text textToAdd = new Text(s);
                    textToAdd.setFont(Font.font(fontName, fontSize));
                    arrayList.get(linerow).add(textToAdd);
                    if (curPosX + (int) Math.round(textToAdd.getLayoutBounds().getWidth())> WINDOW_WIDTH-5){
                        wordWarpe(arrayList.get(linerow),linerow+1);
                        curPosX += (int) Math.round(textToAdd.getLayoutBounds().getWidth());
                        renderAfterWordWarp();
                    }
                    else{
                        curPosX += (int) Math.round(textToAdd.getLayoutBounds().getWidth());
                        linecol++;}
                    render();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.BACK_SPACE){
                    LinkedListDeque line = arrayList.get(linerow);
                    if (line.getIndexOfLetterCursorIsOn() == - 1){
                        if (linerow  > 0){
                            linerow--;
                            line = arrayList.get(linerow);
                            linecol = arrayList.size();
                            curPosX = line.getLineWidth();
                            if (curPosX == 0)
                                curPosX = 5;
                            Text dummyText = new Text("A");
                            dummyText.setFont(Font.font(fontName, fontSize));
                            curPosY -= (int) Math.round(dummyText.getLayoutBounds().getHeight());
                            if (curPosX != 5){
                                line.setWhatCursorPointsToLast();
                                System.out.println(line.getTextLeftOfCursor());
                            }
                            render();
                        }
                    }
                    else{
                        if (line.getIndexOfLetterCursorIsOn() == -1)
                            return;
                        curPosX -= (int) Math.round(line.getTextLeftOfCursor().getLayoutBounds().getWidth());
                        //curPosX -= (int) Math.round(line.get(linecol-1).getLayoutBounds().getWidth());
                        root.get(indexOfRoot).getChildren().remove(arrayList.get(linerow).delete());//(arrayList.get(linerow).getTextLeftOfCursor());
                        //arrayList.get(linerow).delete();
                        linecol--;
                        render();}
                }
                if (code == KeyCode.UP) {
                    if (linerow == 0)
                        return;
                    int indexOfCursor = arrayList.get(linerow).getIndexOfLetterCursorIsOn();
                    linerow--;
                    LinkedListDeque line = arrayList.get(linerow);
                    line.setWhatCursorPointsToFirst();
                    Text dummyText = new Text("A");
                    dummyText.setFont(Font.font(fontName, fontSize));
                    curPosY -= Math.round(dummyText.getLayoutBounds().getHeight());
                    curPosX = 5;
                    if (indexOfCursor > arrayList.get(linerow).size()-1){
                        indexOfCursor = arrayList.get(linerow).size()-1;
                    }
                    for (int i = 0; i <= indexOfCursor;i++){
                        curPosX += (int) Math.round(line.getTextToRightOfCursor().getLayoutBounds().getWidth());
                        line.increaseLetterCursorIsCurrentlyOn();
                    }
                    render();
                } else if (code == KeyCode.DOWN) {
                    if (linerow == arrayList.size()-1)
                        return;
                    int indexOfCursor = arrayList.get(linerow).getIndexOfLetterCursorIsOn();
                    linerow++;
                    LinkedListDeque line = arrayList.get(linerow);
                    line.setWhatCursorPointsToFirst();
                    Text dummyText = new Text("A");
                    dummyText.setFont(Font.font(fontName, fontSize));
                    curPosY += Math.round(dummyText.getLayoutBounds().getHeight());
                    curPosX = 5;
                    if (indexOfCursor > arrayList.get(linerow).size()-1){
                        indexOfCursor = arrayList.get(linerow).size()-1;
                    }
                    for (int i = 0; i <= indexOfCursor;i++){
                        curPosX += (int) Math.round(line.getTextToRightOfCursor().getLayoutBounds().getWidth());
                        line.increaseLetterCursorIsCurrentlyOn();
                    }
                    render();
                }else if (code == KeyCode.RIGHT) {
                    LinkedListDeque line = arrayList.get(linerow);
                    if (line.getIndexOfLetterCursorIsOn() == line.size() - 1){
                        if ((arrayList.size() - 1 > linerow) && !arrayList.get(linerow+1).isEmpty()){
                            linerow++;
                            line = arrayList.get(linerow);
                            linecol = 0;
                            curPosX = 5;
                            Text dummyText = new Text("A");
                            dummyText.setFont(Font.font(fontName, fontSize));
                            curPosY += (int) Math.round(dummyText.getLayoutBounds().getHeight());
                            line.setWhatCursorPointsToFirst();
                            render();
                        }
                    }
                    else{
                        curPosX += (int) Math.round(line.getTextToRightOfCursor().getLayoutBounds().getWidth());
                        arrayList.get(linerow).increaseLetterCursorIsCurrentlyOn();
                        linecol++;
                        render();}
                }else if (code == KeyCode.LEFT) {
                    LinkedListDeque line = arrayList.get(linerow);
                    if (line.getIndexOfLetterCursorIsOn() == - 1){
                        if (linerow  > 0){
                            linerow--;
                            line = arrayList.get(linerow);
                            linecol = arrayList.size();
                            curPosX = line.getLineWidth();
                            Text dummyText = new Text("A");
                            dummyText.setFont(Font.font(fontName, fontSize));
                            curPosY -= (int) Math.round(dummyText.getLayoutBounds().getHeight());
                            line.setWhatCursorPointsToLast();
                            render();
                        }
                    }else{
                        curPosX -= (int) Math.round(line.getTextLeftOfCursor().getLayoutBounds().getWidth());
                        arrayList.get(linerow).decreaseLetterCursorIsCurrentlyOn();
                        //System.out.println(arrayList.get(linerow).getTextLeftOfCursor());
                        linecol--;
                        render();}
                }
            }
        }
    }

//    public void openFIle(String[] args) {
//        if (args.length == 0){
//            System.out.println("No Filename Provided");
//        }
//        if (args.length < 2) {
//            outFile = args[1];
//            if (outFile.equals("debug"));
//            System.exit(1);
//        }
//        String inputFilename = args[0];
//        String outputFilename = args[1];
//
//        try {
//            File inputFile = new File(inputFilename);
//            if (!inputFile.exists()) {
//                System.out.println("Unable to copy because file with name " + inputFilename
//                        + " does not exist");
//                return;
//            }
//            FileReader reader = new FileReader(inputFile);
//
//            BufferedReader bufferedReader = new BufferedReader(reader);
//
//            FileWriter writer = new FileWriter(outputFilename);
//
//            int intRead = -1;
//
//            while ((intRead = bufferedReader.read()) != -1) {
//                char charRead = (char) intRead;
//                if (!String.valueOf(charRead).equals("\r")){
//                    Text s = new Text(String.valueOf(charRead));
//                    s.setTextOrigin(VPos.TOP);
//                    LinkedListDeque.add(s);
//                }
//            }
//
//            System.out.println("Successfully opened " + inFile);
//            render();
//
//            // Close the reader and writer.
//            bufferedReader.close();
//        } catch (FileNotFoundException fileNotFoundException) {
//            System.out.println("File not found! Exception was: " + fileNotFoundException);
//        } catch (IOException ioException) {
//            System.out.println("Error when copying; exception was: " + ioException);
//        }
//    }

    private void render(){
        //checkIfAddingAtEndOfScreen();
        //root.getChildren().removeAll();
        int posx = 5;
        int posy = 0;
        for (int i = 0; i < arrayList.size();i++){
            //if (arrayList.get(i).getIndexOfLetterCursorIsOn() == -1){
            //    break;
            //}
            for (int j = 0; j < arrayList.get(i).size();j++){
                //for (Text s : arrayList.get(i)){
                Text s = arrayList.get(i).get(j);
                s.setX(posx);
                s.setY(posy);
                s.setFont(Font.font(fontName, fontSize));
                posx += Math.round(s.getLayoutBounds().getWidth());
                arrayList.get(i).setLineWidth(posx);
                s.setTextOrigin(VPos.TOP);
                root.get(indexOfRoot).getChildren().remove(s);
                root.get(indexOfRoot).getChildren().add(s);
            }
            Text dummyText = new Text("A");
            dummyText.setFont(Font.font(fontName, fontSize));
            posy += Math.round(dummyText.getLayoutBounds().getHeight());
            //curPosX = posx;
            posx = 5;
        }
        updateCursor();
    }

    private void updatedWindowRender(){
        //checkIfAddingAtEndOfScreen();
        //root.getChildren().removeAll();
        int posx = 5;
        int posy = 0;
        int newCursorHeight = 0;
        int previousLineWith = 0;
        int cursorPos = 0;
        //for (int i = 0; i < linerow;i++){
        //    cursorPos += arrayList.get(i).size()-1;
        //}
        ArrayList<LinkedListDeque> newArrayList= new ArrayList<LinkedListDeque>();
        newArrayList.add(new LinkedListDeque());
        System.out.println(WINDOW_WIDTH);
        for (int i = 0; i < arrayList.size();i++){
            for (Text s: arrayList.get(i)) {
                if (newArrayList.get(posy).getLineWidth()+s.getLayoutBounds().getWidth()
                        > WINDOW_WIDTH-5){
                    newArrayList.add(new LinkedListDeque());
                    posx = 5;
                    posy++;
                    cursorPos++;
                    previousLineWith = 0;
                    Text dummyText = new Text("A");
                    dummyText.setFont(Font.font(fontName, fontSize));
                    newCursorHeight += Math.round(dummyText.getLayoutBounds().getHeight());
                }
                System.out.println(newArrayList.get(posy).getLineWidth()+s.getLayoutBounds().getWidth());
                cursorPos++;
                posx += (int) Math.round(s.getLayoutBounds().getWidth());
                newArrayList.get(posy).setLineWidth(previousLineWith+posx);
                newArrayList.get(posy).add(s);
                if (s.getText().charAt(0) == ' '){
                    newArrayList.get(posy).setSingleLineToFalse();
                }
            }
            previousLineWith += posx;
            posx = 5;
        }
        //System.out.println(arrayList.get(posy).getLineWidth());
        linerow = posy;
        linecol = arrayList.get(posy).getLineWidth();
        arrayList = newArrayList;
        curPosX = arrayList.get(posy).getLineWidth();
        curPosY = newCursorHeight;
        //setCursor(cursorPos,posy);
        render();
    }

    /*public void setCursor(int cur, int y){
        linerow = 0;
        linecol = 0;
            for (int i = 0; i < arrayList.size()-1;i++){
                while (cur > 0){
                arrayList.get(i).setWhatCursorPointsToFirst();
                        arrayList.get(i).increaseLetterCursorIsCurrentlyOn();
                cur -= arrayList.get(i).size();
                linerow++;
            }
        }
    }*/

    private void wordWarpe(LinkedListDeque list, int posYToAddTo){
        if (posYToAddTo > arrayList.size()-1){
            linecol = 0;
            linerow++;
            curPosX = 5;
            Text dummyText = new Text("A");
            dummyText.setFont(Font.font(fontName, fontSize));
            curPosY += Math.round(dummyText.getLayoutBounds().getHeight());
            arrayList.add(new LinkedListDeque());
            //while(list.getIndexOfLetterCursorIsOn() != list.size() - 1){
            //    arrayList.get(posYToAddTo).add(list.delete());
            //}
            boolean one = list.getIfSingleLetter();
            if (one == true){
                arrayList.get(linerow).add(list.delete());}
            else{
                while (list.getTextLeftOfCursor().getText().charAt(0) != ' '){
                    //curPosX += (int) Math.round(list.getTextLeftOfCursor().getLayoutBounds().getWidth());
                    arrayList.get(linerow).addFirst(arrayList.get(linerow-1).delete());
                    linecol++;
                    arrayListSize++;
                    arrayList.get(linerow).setWhatCursorPointsToLast();
                }
            }
            //updateCursor();
        }
    }
    private void renderAfterWordWarp(){
        //checkIfAddingAtEndOfScreen();
        //root.getChildren().removeAll();
        int posx = 5;
        int posy = 0;
        for (int i = 0; i < arrayList.size();i++){
            for (int j = 0; j < arrayList.get(i).size();j++){
                Text s = arrayList.get(i).get(j);
                s.setX(posx);
                s.setY(posy);
                s.setFont(Font.font(fontName, fontSize));
                posx += Math.round(s.getLayoutBounds().getWidth());
                arrayList.get(i).setLineWidth(posx);
                s.setTextOrigin(VPos.TOP);
                root.get(indexOfRoot).getChildren().remove(s);
                root.get(indexOfRoot).getChildren().add(s);
            }
            Text dummyText = new Text("A");
            dummyText.setFont(Font.font(fontName, fontSize));
            posy += Math.round(dummyText.getLayoutBounds().getHeight());
            curPosX = posx;
            posx = 5;
        }
        updateCursor();
    }
    private void updateCursor() {
        // Re-position the text.
        // Re-size and re-position the bounding box.
        if (arrayList.get(linerow).isEmpty()){
            root.get(indexOfRoot).getChildren().remove(cursor);
            Text dummyText = new Text("A");
            dummyText.setFont(Font.font(fontName, fontSize));
            cursor.setHeight(dummyText.getLayoutBounds().getHeight());
            cursor.setWidth(1);

            // For rectangles, the position is the upper left hand corner.
            cursor.setX(curPosX);
            cursor.setY(curPosY);
            // Many of the JavaFX classes have implemented the toString() function, so that
            // they print nicely by default.
            //System.out.println("Bounding box: " + textBoundingBox);
            root.get(indexOfRoot).getChildren().add(cursor);
        }
        else{
            root.get(indexOfRoot).getChildren().remove(cursor);
            Text dummyText = new Text("A");
            dummyText.setFont(Font.font(fontName, fontSize));
            cursor.setHeight(dummyText.getLayoutBounds().getHeight());
            // For rectangles, the position is the upper left hand corner.
            cursor.setX(curPosX);
            cursor.setY(curPosY);
            // Many of the JavaFX classes have implemented the toString() function, so that
            // they print nicely by default.
            //System.out.println("Bounding box: " + textBoundingBox);
            root.get(indexOfRoot).getChildren().add(cursor);}
        // Make sure the text appears in front of the rectangle.
    }

    private class CursorBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        CursorBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    /** Makes the text bounding box change color periodically. */
    public void makeCursorBlink() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinkEventHandler cursorChange = new CursorBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /** A Text object that will be used to print the current mouse position. */
        Text positionText;

        MouseClickEventHandler(Group root) {
            // For now, since there's no mouse position yet, just create an empty Text object.
            positionText = new Text("");
            // We want the text to show up immediately above the position, so set the origin to be
            // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the
            // text).
            positionText.setTextOrigin(VPos.BOTTOM);

            // Add the positionText to root, so that it will be displayed on the screen.
            root.getChildren().add(positionText);
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            // Because we registered this EventHandler using setOnMouseClicked, it will only called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();

            // Display text right above the click.
            positionText.setText("(" + mousePressedX + ", " + mousePressedY + ")");
            positionText.setX(mousePressedX);
            positionText.setY(mousePressedY);
        }
    }

    public void resize(Scene scene,ScrollBar scrollBar){
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                // Re-compute Allen's width.
                WINDOW_WIDTH = newScreenWidth.intValue();
                //wordWarpe(arrayList.get(0),0);
                scrollBar.setOrientation(Orientation.VERTICAL);
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
                scrollBar.setMin(100);
                scrollBar.setMax(WINDOW_HEIGHT);
                WINDOW_WIDTH = WINDOW_WIDTH - (int)Math.round(scrollBar.getLayoutBounds().getWidth());
                scrollBar.setLayoutX(WINDOW_WIDTH);
                updatedWindowRender();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                WINDOW_HEIGHT = newScreenHeight.intValue();
                //WINDOW_HEIGHT = WINDOW_HEIGHT - (int)Math.round(scrollBar.getLayoutBounds().getHeight());
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
                scrollBar.setMax(WINDOW_HEIGHT);
                updatedWindowRender();
            }
        });

    }

    public void scrollBarMoved(ScrollBar scrollBar){
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                // newValue describes the value of the new position of the scroll bar. The numerical
                // value of the position is based on the position of the scroll bar, and on the min
                // and max we set above. For example, if the scroll bar is exactly in the middle of
                // the scroll area, the position will be:
                //      scroll minimum + (scroll maximum - scroll minimum) / 2
                // Here, we can directly use the value of the scroll bar to set the height of Josh,
                // because of how we set the minimum and maximum above.
//                Group textRoot = new Group();
//                root.add(textRoot);
//                indexOfRoot++;
//                textRoot.setLayoutY(10);
            }
        });
    }
    //    public static void saveFile(){
//        outFile = inFile;
//        try {
//            File inputFile = new (File(infile));
//
//            if (!inputFile.exists()){
//                System.out.println("unable.....");
//                return;
//            }
//            FileReader reader = new FileReader(inputFile);
//            BufferedReader bufferedReader = new BufferedReader();
//
//            FileWriter writer = new FileWriter(outFile);
//
//            int intRead = -1;
//
//            while ((intRead = bufferedReader.read() != -1)){
//
//            }
//        }
//    }
    @Override
    public void start(Stage primaryStage) {
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        //Group root = new Group();
        Scene scene = new Scene(root.get(indexOfRoot), WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(/*root, WINDOW_WIDTH, WINDOW_HEIGHT*/);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler(root.get(indexOfRoot)));

        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        scrollBar.setMin(100);
        scrollBar.setMax(WINDOW_HEIGHT);

        // All new Nodes need to be added to the root in order to be displayed.
        root.get(indexOfRoot).getChildren().add(cursor);
        root.get(indexOfRoot).getChildren().add(scrollBar);
        updateCursor();
        makeCursorBlink();

        WINDOW_WIDTH = WINDOW_WIDTH - (int)Math.round(scrollBar.getLayoutBounds().getWidth());
        scrollBar.setLayoutX(WINDOW_WIDTH);
        resize(scene, scrollBar);
        scrollBarMoved(scrollBar);
        primaryStage.setTitle("Editor");

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}