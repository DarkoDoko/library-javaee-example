package com.library.app.commontests.category;

import com.library.app.category.model.Category;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;

/**
 *
 * @author ddoko
 */
@Ignore
public class CategoryForTestsRepository {

    public static Category java() {
        return new Category("java");
    }

    public static Category cleanCode() {
        return new Category("Clean Code");
    }

    public static Category architecture() {
        return new Category("Architecture");
    }

    public static Category networks() {
        return new Category("Networks");
    }
    
    public static List<Category> allCategories() {
        return Arrays.asList(java(), cleanCode(), architecture(), networks());
    }

}
