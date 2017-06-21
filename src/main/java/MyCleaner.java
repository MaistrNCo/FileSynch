import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.stream.Stream;

class MyCleaner<P extends Path> extends SimpleFileVisitor<P> {
    private Path source;
    private Path destination;

    public MyCleaner(Path source, Path destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public FileVisitResult postVisitDirectory(P dir, IOException exc) throws IOException {
        checkAndDelete(dir);
        return FileVisitResult.CONTINUE;
    }

    private void checkAndDelete(Path dir) throws IOException {
        Stream<Path> destPathContent = Files.list(dir);
        Iterator<Path> iterator = destPathContent.iterator();
        while (iterator.hasNext()) {
            Path path = iterator.next();
            Path relativePath = destination.relativize(path);
            Path currSourcePath = source.resolve(relativePath);
            if (Files.notExists(currSourcePath)) {
                try {
                    Files.delete(path);
                    System.out.println("\tdeleted " + path);
                } catch (AccessDeniedException e) {
                    System.out.println("\tAccess denied to directory " + path);
                } catch (DirectoryNotEmptyException e){
                    System.out.println("\tDirectory " + path + " is not empty");
                }
            }
        }
        destPathContent.close();
    }

}
