# SimplePhotoPicker
[![](https://jitpack.io/v/jaychang0917/SimplePhotoPicker.svg)](https://jitpack.io/#jaychang0917/SimplePhotoPicker)

A photo picker powered by RxJava2

## Installation
In your project level build.gradle :

```java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

In your app level build.gradle :

```java
dependencies {
    compile 'com.github.jaychang0917:SimplePhotoPicker:{latest_version}'
}
```
[![](https://jitpack.io/v/jaychang0917/SimplePhotoPicker.svg)](https://jitpack.io/#jaychang0917/SimplePhotoPicker)


```java
private void pickPhotosFromAlbum() {
  SimplePhotoPicker.with(this)
    .toolbarColor(R.color.colorPrimary)
    .statusBarColor(R.color.colorPrimary)
    .selectedBorderColor(R.color.colorPrimary)
    .selectedIcon(R.drawable.ic_add)
    .isTitleCenter(true)
    .title("Title")
    .actionText(R.string.add)
    .columnCount(3)
    .limit(6)
    .pickMultiPhotosFromAlbum()
    .subscribe(uris -> {
      Log.d(TAG, "uris size: " + uris.size());
    });
}

private void takePhotoFromCamera() {
  SimplePhotoPicker.with(this)
    .takePhotoFromCamera()
    .subscribe(uri -> {
      Log.d(TAG, "uri: " + uri);
    });
}
```

## License
```
Copyright 2017 Jay Chang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
