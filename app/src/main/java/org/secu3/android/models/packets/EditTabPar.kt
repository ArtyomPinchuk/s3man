package org.secu3.android.models.packets

data class EditTabPar(
    var index: Int,
    var codeTab: Int,
    var address: Int,
    var data: String
)

/** EDITAB_PAR
 *
    - * d A B CC (data) \r -

A	Индекс набора таблиц (тип топлива)
B	Код таблицы в наборе
СС	Байтовый адрес внутри таблицы для принимаемых и передаваемых данных
(data)	Блок данных содержащий не более 16-ти байт */
