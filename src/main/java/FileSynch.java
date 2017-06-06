import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.stream.Stream;


class FileSynch {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Wrong parameters amount ");
            return;
        }

        String source = args[0];
        String destination = args[1];
        if (destination.contains(source)) {
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
                    copyDirectoriesToDest(dir);
                    return FileVisitResult.CONTINUE;
                }

                private void copyDirectoriesToDest(Path dir) {
                    Path currTargetPath = Paths.get(dir.toString().replaceFirst(source, destination));
                    try {
                        Files.copy(dir, currTargetPath);
                        System.out.println("copied  directory: " + currTargetPath);
                    } catch (FileAlreadyExistsException e) {
                        System.out.println("path already exist: " + currTargetPath);
                    } catch (IOException e) {
                        System.out.println("unsuccessful copy of " + dir);
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    copyFilesToDest(file);
                    return FileVisitResult.CONTINUE;
                }

                private void copyFilesToDest(Path file) throws IOException {
                    Path currTargetPath = Paths.get(file.toString().replaceFirst(pathIn.toString(), pathOut.toString()));
                    try {
                        Files.copy(file, currTargetPath);
                        System.out.println("copied  file: " + currTargetPath);
                    } catch (FileAlreadyExistsException e) {
                        if (Files.size(file) != Files.size(currTargetPath)) {
                            Files.copy(file, currTargetPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("file rewritten: " + currTargetPath);
                        } else {
                            System.out.println("file already exist: " + currTargetPath);
                        }
                    } catch (IOException e) {
                        System.out.println("unsuccessful copy of " + file);
                    }
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    removeDeletedContent(dir);
                    return FileVisitResult.CONTINUE;
                }

                private void removeDeletedContent(Path dir) throws IOException {
                    Path currTargetPath = Paths.get(dir.toString().replaceFirst(source, destination));
                    Stream<Path> pathContent = Files.list(currTargetPath);
                    Iterator<Path> iterator = pathContent.iterator();
                    while (iterator.hasNext()) {
                        Path path = iterator.next();
                        if (Files.notExists(Paths.get(path.toString().replaceFirst(destination, source)))) {
                            try {
                                Files.delete(path);
                                System.out.println("deleted " + path);
                            } catch (DirectoryNotEmptyException e){
                                removeDeletedContent(path);
                                Files.delete(path);
                                System.out.println("deleted " + path);
                            }
                        }
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
