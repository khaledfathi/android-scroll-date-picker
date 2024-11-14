
# Row Scroll Date Picker [![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)

an Android XML row scroll date picker

## Screenshots

![Scroll_date_picker](https://github.com/khaledfathi/readme_images/blob/main/android-scroll-date-picker/a.png?raw=true)

![Scroll_date_picker](https://github.com/khaledfathi/readme_images/blob/main/android-scroll-date-picker/b.png?raw=true)

![Scroll_date_picker](https://github.com/khaledfathi/readme_images/blob/main/android-scroll-date-picker/c.png?raw=true)

## Installation

go to your app build.gradle and add these requirements

sdk required

```gradle.kts
android {
  ...
  compileSdk = 35
}
```

add implementation

```gradle.kts
dependencies {
  ...   
  implementation("io.github.khaledfathi:scroll_date_picker:0.1.1")
}
```



## Usage/Examples

create xml element

```xml
<com.kinsidelibs.scroll_date_picker.ScrollDatePicker
  android:id="@+id/date_picker"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"/>
```

XML attributes :
```xml
<com.kinsidelibs.scroll_date_picker.ScrollDatePicker
  ...
  app:selected_color = "YOUR COLOR"
  app:unselected_color = "YOUR COLOR"
  app:today_date_color = "YOUR COLOR"
  app:day_name_color = "YOUR COLOR"
  app:month_name_color = "YOUR COLOR"
  app:day_number_color = "YOUR COLOR"
  app:today_button_color = "YOUR COLOR"
  app:today_button_text_color = "YOUR COLOR"
  app:full_date_color = "YOUR COLOR"
  app:full_date_under_line_color ="YOUR COLOR"
  app:scroll_background = "YOUR COLOR"/>
```

date range by default :

1/1/2010 ~ 31/12/2040

you can set the date range as following

```kotlin
val datePicker = findViewById<ScrollDatePicker>(R.id.date_picker)
datePicker.setDateRange(
    2020,1,1,
    2025,12,31
)
```

get the current selected date

```kotlin
val datePicker = findViewById<ScrollDatePicker>(R.id.date_picker)
datePicker.getCurrentSelectedDate()
```

listen to date change :

you can deal with [LocalDate] class by  using :

```kotlin
val datePicker = findViewById<ScrollDatePicker>(R.id.date_picker)
  datePicker.setOnDateChange { date->
      //current selected date 
      date.localDateObject
      //date details 
      val day = date.day
      val month= date.month
      val year= date.year
  }
```
or you can get value directly by using :

```kotlin
val datePicker = findViewById<ScrollDatePicker>(R.id.date_picker)
  datePicker.setOnDateChange { date->
      //current selected date values
      val day = date.day
      val month = date.month
      val year = date.year
  }
```