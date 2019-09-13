package by.milosh.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookFilter {

    private List<String> authors;
    private List<String> genres;
    private Integer startDate;
    private Integer endDate;

    public boolean validateFilter() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Object o = field.get(this);
                if (o != null) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
