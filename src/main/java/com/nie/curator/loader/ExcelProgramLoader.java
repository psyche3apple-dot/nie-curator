// src/main/java/com/nie/curator/loader/ExcelProgramLoader.java
@Component
@Slf4j
public class ExcelProgramLoader {
    private final List<Program> programs = new ArrayList<>();

    @PostConstruct
    public void loadExcelData() {
        try (InputStream is = new ClassPathResource("C:\Users\eco\Desktop/program data.xlsx").getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 헤더 스킵
                
                // 각 열 데이터를 읽어 Program 객체로 매핑
                programs.add(Program.builder()
                        .id((int) getNumericValue(row.getCell(0)))
                        .category(getStringValue(row.getCell(1)))
                        .targetGroups(parseMultiValues(getStringValue(row.getCell(2))))
                        // ... (나머지 필드 매핑)
                        .gradeRange(parseGrade(getStringValue(row.getCell(6))))
                        .build());
            }
            log.info("총 {}개의 프로그램이 메모리에 적재되었습니다.", programs.size());
        } catch (Exception e) {
            log.error("Excel 데이터 로드 실패", e);
        }
    }

    public List<Program> getAllPrograms() { return programs; }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        return cell.getStringCellValue().trim();
    }

    private Set<String> parseMultiValues(String value) {
        if (value.isEmpty()) return Collections.emptySet();
        // "개인·단체" -> ["개인", "단체"] 등 분리 로직 적용
        String processed = value.replace("·", ","); 
        return Arrays.stream(processed.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    private GradeRange parseGrade(String value) {
        if (value.contains("전체")) return new GradeRange(null, null, true);
        String[] parts = value.replace("학년", "").split("~");
        return new GradeRange(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), false);
    }
}