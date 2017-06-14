import java.io.IOException;
import java.nio.file.*;

class FileSynch {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Wrong parameters amount ");
            return;
        }
        if (Files.isDirectory(Paths.get(args[0])) && Files.isDirectory(Paths.get(args[1]))) {
            String source = args[0];
            String destination = args[1];
            if (destination.contains(source)) {
                System.out.println("Destination directory is not allowed inside source path!");
                return;
            }
            FileSynch fs = new FileSynch();
            fs.sync(Paths.get(source), Paths.get(destination));
        } else {
            System.out.println("Such directories not found");
        }
    }

    public void sync(Path source, Path destination) {
        try {
            Files.walkFileTree(destination, new MyCleaner<>(source, destination));
            Files.walkFileTree(source, new MyCopier<>(source, destination));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
