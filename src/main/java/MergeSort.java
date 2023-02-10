import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
@Getter
@Setter
public class MergeSort {

    private final String currentDir = System.getProperty("user.dir");
    private String sortMode;
    private String typeOfData;
    private String outputFileName;
    private final List<String> inputFileName = new ArrayList<>();
    private final long tempFileSize = Runtime.getRuntime().totalMemory();

    public void setInputFileName(String name) {
        this.inputFileName.add(currentDir+"\\" + name);
    }

    public void setArgs(String[] args) throws IOException {
        boolean FlagSortMode = false;
        int countOfInputFile;

        for (String s : args
        ) {
            if (s.endsWith(".txt")) {
                if (outputFileName == null) {
                    this.setOutputFileName(currentDir+"\\" +s);
                } else {
                    this.setInputFileName(s);
                }
                continue;
            }
            switch (s) {
                case "-a" -> {
                    this.setSortMode("-a");
                    FlagSortMode = true;
                }
                case "-d" -> {
                    this.setSortMode("-d");
                    FlagSortMode = true;
                }
                case "-s" -> this.setTypeOfData("-s");
                case "-i" -> this.setTypeOfData("-i");
            }
        }
        if (sortMode == null) {
            this.setSortMode("-a");
        }
        if (typeOfData == null) {
            throw new IOException("""
                    Не введен тип данных формат ввода должен быть следующим:
                    -i - для чисел;
                    -s - для строк""");
        }
        if (outputFileName == null) {
            throw new IOException("Не задано имя файла для результирующих данных, файл должен иметь расширение .txt");
        }
        if (inputFileName.isEmpty())
            throw new IOException("Не введено ни одного файли с входными данными, файл должен располагаться в папке с jar файлом и иметь расширение .txt");

        countOfInputFile = FlagSortMode ? args.length - 3 : args.length - 2;

        if (countOfInputFile != inputFileName.size()) {
            System.out.println("Некоторые из имен файлов с входными данными некорректны и обработаны не будут (файлы должны иметь расширение .txt)");
        }
    }

    public void sort() {
        List<String> result = new LinkedList<>();
        List<File> tempFiles = new LinkedList<>();
        File outputFile = new File(outputFileName);
        Pattern pattern = Pattern.compile("([^A-Za-zА-Яа-я\\d\\n])");
        for (String name : inputFileName
        ) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(name))) {
                while (fileReader.ready()) {
                    String findString = fileReader.readLine();
                    Matcher matcher = pattern.matcher(findString);
                    if (!matcher.find()) {
                        result.add(findString);
                    }
                    if (result.size() > tempFileSize) {
                        File tempFile = File.createTempFile("tempFile", ".txt");
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                            if (sortMode.equals("-d")) {
                                Collections.reverse(result);
                            }
                            writeToFile(result, writer);
                            tempFiles.add(tempFile);
                            result.clear();
                            tempFile.deleteOnExit();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                File tempFile = File.createTempFile("tempFile", ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                    if (sortMode.equals("-d")) {
                        Collections.reverse(result);
                    }
                    writeToFile(result, writer);
                    tempFiles.add(tempFile);
                    result.clear();
                    tempFile.deleteOnExit();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        if (typeOfData.equals("-i")) {
            resultMergeInt(outputFile, tempFiles, sortMode);
        } else resultMergeString(outputFile, tempFiles, sortMode);
    }

    private void resultMergeInt(File outputFile, List<File> tempFiles, String sortMode) {
        List<Integer> listOfElements = new ArrayList<>();
        List<BufferedReader> listOfBuffers = new ArrayList<>();
        Integer indexBuffers = null;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (File f : tempFiles
            ) {
                BufferedReader fileReader = new BufferedReader(new FileReader(f));
                if (fileReader.ready()) {
                    listOfBuffers.add(fileReader);
                }
            }
            while (true) {
                try {
                    if (indexBuffers != null) {
                        if (listOfBuffers.get(indexBuffers).ready()) {
                            if (listOfElements.get(indexBuffers) == null) {
                                listOfElements.set(indexBuffers, Integer.parseInt(listOfBuffers.get(indexBuffers).readLine()));
                            }
                        } else {
                            listOfElements.remove(null);
                        }
                    } else {
                        for (BufferedReader br : listOfBuffers
                        ) {
                            if (listOfElements.size() != listOfBuffers.size()) {
                                if (br.ready()) {
                                    listOfElements.add(Integer.parseInt(br.readLine()));
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Элемент не может быть преобразован в число и будет пропущен");
                    System.out.println(e.getMessage());
                    continue;
                }
                if (listOfElements.isEmpty()) {
                    break;
                }
                if (sortMode.equals("-a")) {
                    if (listOfElements.stream().min(Integer::compareTo).stream().findFirst().isPresent()) {
                        Integer elemForWrite = listOfElements.stream().min(Integer::compareTo).get();
                        writer.append(elemForWrite.toString());
                        writer.append(System.lineSeparator());
                        indexBuffers = listOfElements.indexOf(elemForWrite);
                        listOfElements.set(indexBuffers, null);
                    } else {
                        listOfBuffers.get(indexBuffers).close();
                        listOfBuffers.remove(indexBuffers.intValue());
                    }
                } else {
                    if (listOfElements.stream().max(Integer::compareTo).stream().findFirst().isPresent()) {
                        Integer elemForWrite = listOfElements.stream().max(Integer::compareTo).get();
                        writer.append(elemForWrite.toString());
                        writer.append(System.lineSeparator());
                        indexBuffers = listOfElements.indexOf(elemForWrite);
                        listOfElements.set(indexBuffers, null);
                    } else {
                        listOfBuffers.get(indexBuffers).close();
                        listOfBuffers.remove(indexBuffers.intValue());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            for (BufferedReader br : listOfBuffers
            ) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void resultMergeString(File outputFile, List<File> tempFiles, String sortMode) {
        List<String> listOfElements = new ArrayList<>();
        List<BufferedReader> listOfBuffers = new ArrayList<>();
        Integer indexBuffers = null;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (File f : tempFiles
            ) {
                BufferedReader fileReader = new BufferedReader(new FileReader(f));
                if (fileReader.ready()) {
                    listOfBuffers.add(fileReader);
                }
            }
            while (true) {
                if (indexBuffers != null) {
                    if (listOfBuffers.get(indexBuffers).ready()) {
                        if (listOfElements.get(indexBuffers) == null) {
                            listOfElements.set(indexBuffers, listOfBuffers.get(indexBuffers).readLine());
                        }
                    } else {
                        listOfElements.remove(null);
                    }
                } else {
                    for (BufferedReader br : listOfBuffers
                    ) {
                        if (listOfElements.size() != listOfBuffers.size()) {
                            if (br.ready()) {
                                listOfElements.add(br.readLine());
                            }
                        }
                    }
                }
                if (listOfElements.isEmpty()) {
                    break;
                }
                if (sortMode.equals("-a")) {
                    if (listOfElements.stream().min(String::compareTo).stream().findFirst().isPresent()) {
                        String elemForWrite = listOfElements.stream().min(String::compareTo).get();
                        writer.append(elemForWrite);
                        writer.append(System.lineSeparator());
                        indexBuffers = listOfElements.indexOf(elemForWrite);
                        listOfElements.set(indexBuffers, null);
                    } else {
                        listOfBuffers.get(indexBuffers).close();
                        listOfBuffers.remove(indexBuffers.intValue());
                    }
                } else {
                    if (listOfElements.stream().max(String::compareTo).stream().findFirst().isPresent()) {
                        String elemForWrite = listOfElements.stream().max(String::compareTo).get();
                        writer.append(elemForWrite);
                        writer.append(System.lineSeparator());
                        indexBuffers = listOfElements.indexOf(elemForWrite);
                        listOfElements.set(indexBuffers, null);
                    } else {
                        listOfBuffers.get(indexBuffers).close();
                        listOfBuffers.remove(indexBuffers.intValue());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            for (BufferedReader br : listOfBuffers
            ) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void writeToFile(List<String> result, BufferedWriter writer) throws IOException {
        for (String a : result) {
            writer.write(a);
            writer.write(System.lineSeparator());
        }
    }
}
