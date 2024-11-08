package ru.otus.java.basic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class FileManager {

    private File currentDirectory;

    public FileManager() {
        this.currentDirectory = new File(System.getProperty("user.dir"));
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Консольный файловый менеджер. Введите 'help' для списка команд.");

        while (true) {
            System.out.print(currentDirectory.getAbsolutePath() + " > ");
            command = scanner.nextLine().trim();
            if (command.equals("exit")) {
                break;
            }
            processCommand(command);
        }

        scanner.close();
    }

    private void processCommand(String command) {
        String[] parts = command.split(" ");
        String mainCommand = parts[0];

        try {
            switch (mainCommand) {
                case "ls":
                    listFiles(parts.length > 1 && parts[1].equals("-i"));
                    break;
                case "cd":
                    changeDirectory(parts);
                    break;
                case "mkdir":
                    createDirectory(parts);
                    break;
                case "rm":
                    removeFile(parts);
                    break;
                case "mv":
                    moveFile(parts);
                    break;
                case "cp":
                    copyFile(parts);
                    break;
                case "finfo":
                    fileInfo(parts);
                    break;
                case "help":
                    printHelp();
                    break;
                case "find":
                    findFile(parts);
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void listFiles(boolean detailed) throws IOException {
        File[] files = currentDirectory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (detailed) {
                    System.out.printf("%s - %d байт - %s%n",
                            file.getName(),
                            file.length(),
                            Files.getLastModifiedTime(file.toPath()));
                } else {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("Каталог пуст.");
        }
    }

    private void changeDirectory(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Не указан путь к директории.");
            return;
        }

        File targetDir = new File(currentDirectory, parts[1]);
        if (targetDir.isDirectory()) {
            currentDirectory = targetDir;
        } else if (parts[1].equals("..")) {
            currentDirectory = currentDirectory.getParentFile();
        } else {
            System.out.println("Директория не найдена.");
        }
    }

    private void createDirectory(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Не указано имя директории.");
            return;
        }

        File newDir = new File(currentDirectory, parts[1]);
        if (!newDir.exists()) {
            newDir.mkdir();
            System.out.println("Директория создана: " + newDir.getName());
        } else {
            System.out.println("Директория с таким именем уже существует.");
        }
    }

    private void removeFile(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Не указано имя файла или директории для удаления.");
            return;
        }

        File fileToRemove = new File(currentDirectory, parts[1]);
        if (fileToRemove.exists()) {
            if (fileToRemove.isDirectory() && fileToRemove.list().length != 0) {
                System.out.println("Невозможно удалить непустую директорию.");
            } else {
                fileToRemove.delete();
                System.out.println("Удалено: " + fileToRemove.getName());
            }
        } else {
            System.out.println("Файл или директория не найдены.");
        }
    }

    private void moveFile(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Не указаны источник и место назначения.");
            return;
        }

        File sourceFile = new File(currentDirectory, parts[1]);
        File destinationFile = new File(currentDirectory, parts[2]);
        if (sourceFile.exists()) {
            if (destinationFile.exists()) {
                System.out.print("Файл " + destinationFile.getName() + " уже существует. Переопределить? (y/n): ");
                Scanner scanner = new Scanner(System.in);
                String response = scanner.nextLine();
                if (!response.equalsIgnoreCase("y")) {
                    return;
                }
            }
            sourceFile.renameTo(destinationFile);
            System.out.println("Перемещено: " + sourceFile.getName() + " -> " + destinationFile.getName());
        } else {
            System.out.println("Файл-источник не найден.");
        }
    }

    private void copyFile(String[] parts) throws IOException {
        if (parts.length < 3) {
            System.out.println("Не указаны источник и место назначения.");
            return;
        }

        File sourceFile = new File(currentDirectory, parts[1]);
        File destinationFile = new File(currentDirectory, parts[2]);
        if (sourceFile.exists()) {
            if (destinationFile.exists()) {
                System.out.print("Файл " + destinationFile.getName() + " уже существует. Переопределить? (y/n): ");
                Scanner scanner = new Scanner(System.in);
                String response = scanner.nextLine();
                if (!response.equalsIgnoreCase("y")) {
                    return;
                }
            }
            Files.copy(sourceFile.toPath(), destinationFile.toPath());
            System.out.println("Скопировано: " + sourceFile.getName() + " -> " + destinationFile.getName());
        } else {
            System.out.println("Файл-источник не найден.");
        }
    }

    private void fileInfo(String[] parts) throws IOException {
        if (parts.length < 2) {
            System.out.println("Не указано имя файла.");
            return;
        }

        File file = new File(currentDirectory, parts[1]);
        if (file.exists()) {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            System.out.printf("Имя: %s%nРазмер: %d байт%nДата последнего изменения: %s%n",
                    file.getName(),
                    attrs.size(),
                    attrs.lastModifiedTime());
        } else {
            System.out.println("Файл не найден.");
        }
    }

    private void printHelp() {
        System.out.println("Поддерживаемые команды:");
        System.out.println("ls           - отобразить список файлов в текущем каталоге.");
        System.out.println("ls -i       - отобразить список файлов с подробной информацией.");
        System.out.println("cd [path]   - перейти в указанную директорию.");
        System.out.println("mkdir [name] - создать новую директорию.");
        System.out.println("rm [filename] - удалить указанный файл или директорию.");
        System.out.println("mv [source] [destination] - переместить или переименовать файл/директорию.");
        System.out.println("cp [source] [destination] - скопировать файл.");
        System.out.println("finfo [filename] - получить информацию о файле.");
        System.out.println("help        - вывести список поддерживаемых команд.");
        System.out.println("exit        - завершить работу.");
    }

    private void findFile(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Не указано имя файла для поиска.");
            return;
        }

        String fileName = parts[1];
        searchFile(currentDirectory, fileName);
    }

    private void searchFile(File directory, String fileName) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchFile(file, fileName);
                } else if (file.getName().equals(fileName)) {
                    System.out.println("Найден: " + file.getAbsolutePath());
                }
            }
        }
    }

}