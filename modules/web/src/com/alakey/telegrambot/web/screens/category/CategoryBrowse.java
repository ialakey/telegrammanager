package com.alakey.telegrambot.web.screens.category;

import com.alakey.telegrambot.service.TheMovieDbService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.Category;

import javax.inject.Inject;
import java.util.List;

@UiController("telegrambot_Category.browse")
@UiDescriptor("category-browse.xml")
@LookupComponent("categoriesTable")
@LoadDataBeforeShow
public class CategoryBrowse extends StandardLookup<Category> {

    @Inject
    private DataManager dataManager;
    @Inject
    private TheMovieDbService theMovieDbService;
    @Inject
    private CollectionLoader<Category> categoriesDl;

    @Subscribe("fetchCategoryMovieDbBtn")
    public void onFetchCategoryMovieDbBtnClick(Button.ClickEvent event) {
        theMovieDbService.fetchGenres();
        categoriesDl.load();
    }

    @Subscribe("clearBtn")
    public void onClearBtnClick(Button.ClickEvent event) {
        List<Category> categoryList = dataManager.load(Category.class)
                .list();
        for (Category category: categoryList) {
            dataManager.remove(category);
        }
        categoriesDl.load();
    }
}