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
        if(Files.isDirectory(Paths.get(args[0]))&&Files.isDirectory(Paths.get(args[1]))){
            String source = args[0];
            String destination = args[1];
            if (destination.contains(source)) {
                System.out.println("Destination directory is not allowed inside source path!");
                return;
            }
            FileSynch fs = new FileSynch();
            fs.synchronise(source, destination);
        } else {
            System.out.println("");
        }
    }

    public  void synchronise(String source, String destination) {
        Path pathSource = Paths.get(source);
        Path pathDestination = Paths.get(destination);
        try {
            Files.walkFileTree(pathSource, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    removeDeletedContent(dir);
                    copyDirectoriesToDest(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    copyFilesToDest(file);
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

                private void copyDirectoriesToDest(Path dir) {
                    Path relativePath = pathSource.relativize(dir);
                    Path currDestinationPath = pathDestination.resolve(relativePath);
                    try {
                        Files.copy(dir, currDestinationPath);
                        System.out.println("copied  directory: " + currDestinationPath);
                    } catch (FileAlreadyExistsException e) {
                        System.out.println("path already exist: " + currDestinationPath);
                    } catch (IOException e) {
                        System.out.println("unsuccessful copy of " + dir);
                    }
                }

                private void copyFilesToDest(Path file) throws IOException {
                    Path relativePath = pathSource.relativize(file);
                    Path currDestinationPath = pathDestination.resolve(relativePath);
                    try {
                        Files.copy(file, currDestinationPath);
                        System.out.println("copied  file: " + currDestinationPath);
                    } catch (FileAlreadyExistsException e) {
                        if (Files.size(file) != Files.size(currDestinationPath)) {
                            Files.copy(file, currDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("file rewritten: " + currDestinationPath);
                        } else {
                            System.out.println("file already exist: " + currDestinationPath);
                        }
                    } catch (IOException e) {
                        System.out.println("unsuccessful copy of " + file);
                    }
                }

                private void removeDeletedContent(Path dir) throws IOException {
                    Path relativePath = pathSource.relativize(dir);
                    Path currDestinationPath = pathDestination.resolve(relativePath);
                    Stream<Path> destPathContent = Files.list(currDestinationPath);
                    Iterator<Path> iterator = destPathContent.iterator();
                    while (iterator.hasNext()) {
                        Path path = iterator.next();
                        Path sourcePath = pathDestination.relativize(path);
                        sourcePath = pathSource.resolve(sourcePath);
                        if (Files.notExists(sourcePath)) {
                            try {
                                Files.delete(path);
                                System.out.println("deleted " + path);
                            } catch (DirectoryNotEmptyException e){
                                removeDeletedContent(sourcePath);
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
