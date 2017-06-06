import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class FileSynch {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Wrong parameters amount ");
            return;
        }

        String source = args[0];
        String destination = args[1];
        if (destination.indexOf(source) > -1) {
            System.out.println("Destination directory is not allowed inside source path!");
            return;
        }

        synchronise(source, destination);
    }

    private static void synchronise(String source, String destination) {
        Path pathIn = Paths.get(source);
        Path pathOut = Paths.get(destination);
        try {
            Files.walkFileTree(pathIn, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path currTargetPath = Paths.get(dir.toString().replaceFirst(source, destination));
                    try {
                        Files.copy(dir, currTargetPath);
                        System.out.println("copied  directory: " + currTargetPath);
                    } catch (FileAlreadyExistsException e){
                        System.out.println("path already exist: " + currTargetPath);
                    } catch (IOException e) {
                        System.out.println("unsuccessful copy of " + dir);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path currTargetPath = Paths.get(file.toString().replaceFirst(pathIn.toString(), pathOut.toString()));
                    try {
                        Files.copy(file, currTargetPath);
                        System.out.println("copied  file: " + currTargetPath);
                    } catch (FileAlreadyExistsException e){
                        if (Files.size(file) != Files.size(currTargetPath)) {
                            Files.copy(file, currTargetPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("file rewritten: " + currTargetPath);
                        } else {
                            System.out.println("file already exist: " + currTargetPath);
                        }
                    } catch (IOException e) {
                        System.out.println("unsuccessful copy of " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void findFile(Path path, String fileToFind) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileString = file.toAbsolutePath().toString();
                    if (fileString.endsWith(fileToFind)) {
                        System.out.println("file found at path: " + file.toAbsolutePath());
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
