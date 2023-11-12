package com.alakey.telegrambot.web.screens.category;

import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.Category;

@UiController("telegrambot_Category.edit")
@UiDescriptor("category-edit.xml")
@EditedEntityContainer("categoryDc")
@LoadDataBeforeShow
public class CategoryEdit extends StandardEditor<Category> {
}