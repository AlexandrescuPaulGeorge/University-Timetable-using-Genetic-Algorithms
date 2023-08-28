package com.licenta.aplicatie.schedule;

import com.licenta.aplicatie.models.*;
import com.licenta.aplicatie.repository.RoomRepository;
import com.licenta.aplicatie.repository.SubGroupRepository;
import com.licenta.aplicatie.repository.TeacherRepository;
import com.licenta.aplicatie.repository.TimetableRepository;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component  // Aceasta este o adnotare Spring Boot care permite Spring-ului să recunoască această clasă ca o componentă
public class ExcelExporter {

    // Acestea sunt repository-urile utilizate pentru a interacționa cu baza de date
    private final TimetableRepository timetableRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    private final SubGroupRepository subGroupRepository;

    // Adnotarea Autowired este folosită pentru a injecta automat dependențele în constructor
    @Autowired
    public ExcelExporter(TimetableRepository timetableRepository, TeacherRepository teacherRepository, RoomRepository roomRepository, SubGroupRepository subGroupRepository) {
        this.timetableRepository = timetableRepository;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
        this.subGroupRepository = subGroupRepository;
    }

    // Această metodă caută timetable-ul cu cel mai mare fitness din repository
    private Timetable findTimetableWithHighestFitness() {
        return timetableRepository.findAll()
                .stream()
                .max(Comparator.comparing(Timetable::getFitness))
                .orElse(null);
    }

    // Această metodă exportă timetable-ul într-un fișier Excel
    public void exportTimetable(String filePath) {
        Timetable bestTimetable = findTimetableWithHighestFitness();

        if (bestTimetable == null) {
            return;
        }
        System.out.println("fitness maxim id: " + bestTimetable.getId());

        DayOfWeek[] daysOfWeek = DayOfWeek.values();
        ClassPeriod[] classPeriods = ClassPeriod.values();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet("Profesori");
        Sheet sheet2 = workbook.createSheet("Sali");
        Sheet sheet3 = workbook.createSheet("Orar");
        generateSheetContent1(sheet1, bestTimetable, daysOfWeek, classPeriods);
        generateSheetContent2(sheet2, bestTimetable, daysOfWeek, classPeriods);
        generateSheetContent3(sheet3, bestTimetable, daysOfWeek, classPeriods);
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Această metodă generează conținutul pentru primul sheet al workbook-ului Excel
    private void generateSheetContent1(Sheet sheet, Timetable timetable, DayOfWeek[] daysOfWeek, ClassPeriod[] classPeriods) {
        // Aceasta este lista cu toate sloturile de timp din orar
        List<Timeslot> timeslots = timetable.getTimeSlots();
        // Aceasta este lista cu toți profesorii
        List<Teacher> teachers = teacherRepository.findAll();

        // Creăm un stil de celulă pentru a evidenția celulele roșii
        Workbook workbook = sheet.getWorkbook();
        CellStyle redFontStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        redFontStyle.setFont(font);

        // Creăm rândul pentru capul de tabel
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < teachers.size(); i++) {
            Cell headerCell = headerRow.createCell(i + 2);
            headerCell.setCellValue(teachers.get(i).getName());
        }
        int rowIndex = 1;
        // Pentru fiecare zi și perioadă, verificăm dacă un profesor este programat și îl marcăm cu roșu dacă este cazul
        for (DayOfWeek day : daysOfWeek) {
            int startRow = rowIndex;
            for (ClassPeriod period : classPeriods) {
                Row row = sheet.createRow(rowIndex);
                if (period == ClassPeriod.PERIOD_1) {
                    Cell dayCell = row.createCell(0);
                    dayCell.setCellValue(day.name());
                }
                Cell periodCell = row.createCell(1);
                periodCell.setCellValue(period.getTimePeriod());
                for (int i = 0; i < teachers.size(); i++) {
                    Teacher currentTeacher = teachers.get(i);
                    Cell teacherCell = row.createCell(i + 2);
                    for (Timeslot timeslot : timeslots) {
                        // Dacă un timeslot corespunde cu ziua, perioada și profesorul, atunci celula este marcată ca "OCUPAT"
                        if (timeslot.getDayOfWeek() == day && timeslot.getClassPeriod() == period && timeslot.getTeacher().equals(currentTeacher)) {
                            teacherCell.setCellValue("OCUPAT");
                            teacherCell.setCellStyle(redFontStyle);
                            break;
                        }
                    }
                }
                rowIndex++;
            }
            // combina celulele pentru a avea un singur nume de zi pentru toate perioadele de timp din acea zi
            sheet.addMergedRegion(new CellRangeAddress(startRow, rowIndex - 1, 0, 0));
        }
    }

    private void generateSheetContent2(Sheet sheet, Timetable timetable, DayOfWeek[] daysOfWeek, ClassPeriod[] classPeriods) {
        // Aceasta este lista cu toate sloturile de timp din orar
        List<Timeslot> timeslots = timetable.getTimeSlots();
        // Aceasta este lista cu toate sălile
        List<Room> rooms = roomRepository.findAll();

        // Creăm un stil celulei pentru a evidenția celulele roșii
        Workbook workbook = sheet.getWorkbook();
        CellStyle redFontStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        redFontStyle.setFont(font);

        // Creăm rândul pentru capul de tabel
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < rooms.size(); i++) {
            Cell headerCell = headerRow.createCell(i + 2);
            headerCell.setCellValue(rooms.get(i).getName());
        }
        int rowIndex = 1;
        // Pentru fiecare zi și perioadă, verificăm dacă o sală este ocupată și o marcăm cu roșu dacă este cazul
        for (DayOfWeek day : daysOfWeek) {
            int startRow = rowIndex;
            for (ClassPeriod period : classPeriods) {
                Row row = sheet.createRow(rowIndex);
                if (period == ClassPeriod.PERIOD_1) {
                    Cell dayCell = row.createCell(0);
                    dayCell.setCellValue(day.name());
                }
                Cell periodCell = row.createCell(1);
                periodCell.setCellValue(period.getTimePeriod());
                for (int i = 0; i < rooms.size(); i++) {
                    Room currentRoom = rooms.get(i);
                    Cell roomCell = row.createCell(i + 2);
                    for (Timeslot timeslot : timeslots) {
                        // Dacă un timeslot corespunde cu ziua, perioada și sala, atunci celula este marcată ca "OCUPAT"
                        if (timeslot.getDayOfWeek() == day && timeslot.getClassPeriod() == period && timeslot.getRoom().equals(currentRoom)) {
                            roomCell.setCellValue("OCUPAT");
                            roomCell.setCellStyle(redFontStyle);
                            break;
                        }
                    }
                }
                rowIndex++;
            }
            // combina celulele pentru a avea un singur nume de zi pentru toate perioadele de timp din acea zi
            sheet.addMergedRegion(new CellRangeAddress(startRow, rowIndex - 1, 0, 0));
        }
    }

    private void generateSheetContent3(Sheet sheet, Timetable timetable, DayOfWeek[] daysOfWeek, ClassPeriod[] classPeriods) {
        // Aceasta este lista cu toate sloturile de timp din orar
        List<Timeslot> timeslots = timetable.getTimeSlots();
        // Aceasta este lista cu toate subgrupele
        List<SubGroup> subGroups = subGroupRepository.findAll();
        // Creăm un stil celulei pentru a evidenția celulele negre
        Workbook workbook = sheet.getWorkbook();
        CellStyle blackFontStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        blackFontStyle.setFont(font);
        blackFontStyle.setWrapText(true);

        // Generăm o listă cu toate subiectele unice din timeslots
        List<Subject> subjects = timeslots.stream()
                .map(Timeslot::getSubject)
                .distinct()
                .collect(Collectors.toList());

        // Creăm o hartă cu stiluri de celule specifice pentru fiecare subiect
        Map<Subject, CellStyle> subjectCellStyleMap = createSubjectCellStyleMap(workbook, subjects);

        // Creăm rândul pentru capul de tabel
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < subGroups.size(); i++) {
            Cell headerCell = headerRow.createCell(i + 2);
            headerCell.setCellValue(subGroups.get(i).getName());
        }

        int rowIndex = 1;
        // Pentru fiecare zi și perioadă, verificăm dacă un timeslot corespunde cu acestea și, dacă da, le completăm în foaia Excel
        for (DayOfWeek day : daysOfWeek) {
            int startRow = rowIndex;
            for (ClassPeriod period : classPeriods) {
                Row row = sheet.createRow(rowIndex);
                row.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());

                if (period == ClassPeriod.PERIOD_1) {
                    Cell dayCell = row.createCell(0);
                    dayCell.setCellValue(day.name());
                }

                Cell periodCell = row.createCell(1);
                periodCell.setCellValue(period.getTimePeriod());

                // Căutăm timeslotul care corespunde zilei, perioadei și stilului de predare 'CURS'
                Optional<Timeslot> cursTimeslot = timeslots.stream()
                        .filter(t -> t.getDayOfWeek() == day
                                && t.getClassPeriod() == period
                                && t.getTeachingStyle() == TeachingStyle.CURS)
                        .findFirst();
                // Dacă un astfel de timeslot există, îl completăm în foaie
                if (cursTimeslot.isPresent()) {
                    Cell cursCell = row.createCell(2);
                    fillInTimeslotInfo(cursCell, cursTimeslot.get(), blackFontStyle, subjectCellStyleMap);
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 2, subGroups.size() + 1));
                } else {
                    for (int i = 0; i < subGroups.size(); i++) {
                        sheet.autoSizeColumn(i + 2);
                        SubGroup currentSubGroup = subGroups.get(i);
                        Cell subGroupCell = row.createCell(i + 2);
                        // Căutăm timeslotul pentru subgrupa curentă și stilul de predare 'LABORATOR'
                        Optional<Timeslot> labTimeslot = timeslots.stream()
                                .filter(t -> t.getDayOfWeek() == day
                                        && t.getClassPeriod() == period
                                        && t.getTeachingStyle() == TeachingStyle.LABORATOR
                                        && t.getSubGroup().equals(currentSubGroup))
                                .findFirst();
                        if (labTimeslot.isPresent()) {
                            fillInTimeslotInfo(subGroupCell, labTimeslot.get(), blackFontStyle, subjectCellStyleMap);
                        }
                        // Căutăm timeslotul pentru grupa curentă și stilul de predare 'SEMINAR'
                        Optional<Timeslot> seminarTimeslot = timeslots.stream()
                                .filter(t -> t.getDayOfWeek() == day
                                        && t.getClassPeriod() == period
                                        && t.getTeachingStyle() == TeachingStyle.SEMINAR
                                        && t.getGroup().equals(currentSubGroup.getGroup()))
                                .findFirst();
                        if (seminarTimeslot.isPresent()) {
                            fillInTimeslotInfo(subGroupCell, seminarTimeslot.get(), blackFontStyle, subjectCellStyleMap);
                            if (i < subGroups.size() - 1 && subGroups.get(i + 1).getGroup().equals(currentSubGroup.getGroup())) {
                                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, i + 2, i + 3));
                            }
                        }
                    }
                }
                rowIndex++;
            }
            // combina celulele pentru a avea un singur nume de zi pentru toate perioadele de timp din acea zi
            sheet.addMergedRegion(new CellRangeAddress(startRow, rowIndex - 1, 0, 0));
        }
    }

    private void fillInTimeslotInfo(Cell cell, Timeslot timeslot, CellStyle blackFontStyle, Map<Subject, CellStyle> subjectCellStyleMap) {
        // Generăm textul pentru celulă, care include acronimul subiectului, numele profesorului și numele sălii de clasă
        String cellValue = String.format("%s\n%s\n%s",
                timeslot.getSubject().getAcronym(),
                timeslot.getTeacher().getName(),
                timeslot.getRoom().getName());
        cell.setCellValue(cellValue);
        // Creăm un nou stil pentru celulă clonând stilul de font negru și stilul specific subiectului
        CellStyle newCellStyle = cell.getSheet().getWorkbook().createCellStyle();
        newCellStyle.cloneStyleFrom(blackFontStyle);
        newCellStyle.cloneStyleFrom(subjectCellStyleMap.get(timeslot.getSubject()));

        // Configurăm stilul celulei să aibă un fundal solid și să aibă borders subțiri pe toate laturile
        newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        newCellStyle.setBorderTop(BorderStyle.THIN);
        newCellStyle.setBorderBottom(BorderStyle.THIN);
        newCellStyle.setBorderLeft(BorderStyle.THIN);
        newCellStyle.setBorderRight(BorderStyle.THIN);
        // Aplicăm stilul celulei
        cell.setCellStyle(newCellStyle);
        // Configurăm stilul să permită înfășurarea textului și să centreze conținutul atât orizontal, cât și vertical
        newCellStyle.setWrapText(true);
        newCellStyle.setAlignment(HorizontalAlignment.CENTER);
        newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // Setăm înălțimea rândului în funcție de numărul de linii din valoarea celulei
        int newlineCount = cellValue.split("\n").length;
        cell.getRow().setHeightInPoints(newlineCount * cell.getSheet().getDefaultRowHeightInPoints());
    }

    private Map<Subject, CellStyle> createSubjectCellStyleMap(Workbook workbook, List<Subject> subjects) {
        // Creăm un Map care va asocia fiecare subiect cu un stil de celulă specific
        Map<Subject, CellStyle> subjectCellStyleMap = new HashMap<>();
        for (Subject subject : subjects) {
            // Pentru fiecare subiect, creăm un stil celulei care are un fundal colorat într-o culoare specifică
            CellStyle style = workbook.createCellStyle();
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Culoarea este determinată de ID-ul subiectului și numărul de culori disponibile în IndexedColors
            style.setFillForegroundColor((short) (subject.getId() % IndexedColors.values().length));

            // Adăugăm perechea subiect-stil în Map
            subjectCellStyleMap.put(subject, style);
        }
        // returnam Map-ul generată
        return subjectCellStyleMap;
    }

}
