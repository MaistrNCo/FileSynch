import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MyCopier<P extends Path> implements FileVisitor<P> {
    Path source;
    Path destination;

    public MyCopier(Path source, Path destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public FileVisitResult preVisitDirectory(P dir, BasicFileAttributes attrs) throws IOException {
        copyDirectoriesToDest(dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(P file, BasicFileAttributes attrs) throws IOException {
        copyFilesToDest(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(P file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(P dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    private void copyDirectoriesToDest(P dir) {
        java.nio.file.Path relativePath = source.relativize(dir);
        java.nio.file.Path currDestinationPath = destination.resolve(relativePath);
        try {
            Files.copy(dir, currDestinationPath);
            System.out.println("copied  directory: " + currDestinationPath);
        } catch (FileAlreadyExistsException e) {
            //System.out.println("path already exist: " + currDestinationPath);
        } catch (IOException e) {
            System.out.println("unsuccessful copy of " + dir);
        }
    }

    private void copyFilesToDest(P file) throws IOException {
        java.nio.file.Path relativePath = source.relativize(file);
        java.nio.file.Path currDestinationPath = destination.resolve(relativePath);
        try {
            Files.copy(file, currDestinationPath);
            System.out.println("copied  file: " + currDestinationPath);
        } catch (FileAlreadyExistsException e) {
            if (Files.size(file) != Files.size(currDestinationPath)) {
                Files.copy(file, currDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("file rewritten: " + currDestinationPath);
            } else {
                //System.out.println("file already exist: " + currDestinationPath);
            }
        } catch (IOException e) {
            System.out.println("unsuccessful copy of " + file);
        }
    }

}