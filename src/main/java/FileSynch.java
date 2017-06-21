import java.io.IOException;
import java.nio.file.*;

class FileSynch {

    private static final int SOURCE_ARG_INDEX = 0;
    private static final int DEST_ARG_INDEX = 1;

    public static void main(String[] args) {
        FileSynch fs = new FileSynch();
        if (fs.checkArgs(args)) {
            fs.sync(Paths.get(args[SOURCE_ARG_INDEX]), Paths.get(args[DEST_ARG_INDEX]));
        }
    }

    private boolean checkArgs(String[] args) {

        if (args.length != 2) {
            System.out.println("Wrong parameters amount ");
            return false;
        }

        if (Files.isDirectory(Paths.get(args[SOURCE_ARG_INDEX]))
                && Files.isDirectory(Paths.get(args[DEST_ARG_INDEX]).getParent())) {
            String source = args[SOURCE_ARG_INDEX];
            String destination = args[DEST_ARG_INDEX];
            if (destination.contains(source)) {
                System.out.println("Destination directory is not allowed inside source path!");
                return false;
            }
        } else {
            System.out.println("Such directories not found");
            return false;
        }

        return true;
    }

    public void sync(Path source, Path destination) {
        try {
            Files.walkFileTree(source, new MyCopier<>(source, destination));
            Files.walkFileTree(destination, new MyCleaner<>(source, destination));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
