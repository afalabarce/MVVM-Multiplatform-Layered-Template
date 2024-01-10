# MVVM Kotlin MultiPlatform Template

This template is designed to facilitate the development of cross-platform applications, using
the framework [KMP (Kotlin MultiPlatform)](https://kotlinlang.org/docs/multiplatform.html). Also I know
has provided the necessary dependencies so that, **using a single code base**, we can
build both business and presentation layers.

For the presentation layer, the necessary dependencies have been added to be able to design the
user interfaces using [JetpackCompose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/),
which will result in greater control, and above all, a common code base in which we will not have to
design specific UIs for each platform.

Below, both the preconfigurations to be made and an exhaustive explanation will be explained.
of what the template offers in terms of architecture, libraries and deployed functionalities.

## Pre-configuration

The first time we create a multiplatform project, we will need several previous configurations, in which
MacOS environments (development for iOS has its little things), which will help us in the correct configuration
of the system, avoiding later compilation errors. Therefore, we must pay special attention to
The next points:

- **Common to all Operating Systems (Windows, MacOs, Linux)**:
    - Install JDK 17, if we do not have it installed yet.
    - Add a local.properties file to the root of the project in which we establish the path
      to the Android SDK.
    - Add the plugin [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- **Specific on MacOs systems (for iOS development)**:
    - Check that the system meets the minimum requirements for KMP developments for iOS with
      [KDoctor](https://github.com/Kotlin/kdoctor).

## Structure of a KMP module

Every project defined for **K**otlin **M**ulti**P**latform has a clearly defined structure, which will allow us
the generation of custom code on each platform, having the possibility of, for example, defining a function in a common way but that,
Depending on the platform, it must be initialized in one way or another.

Let's see below the typical structure for a KMP module (in this case, for a module that supports the platforms
Android, iOS and JVM - Desktop -):

| Module structure | |
|---------------------------------------------|:-- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -----------------------|
| ![kmp_structure.png](art/kmp_structure.png) | As we can see, this structure is the most complex that we are going to obtain, since apart from the specific code modules of each platform, we also have its corresponding testing module. Let's see below the particularities of this structure. |

Each folder defined in the module will contain exactly the same folder structure (package), so that in the event that we need to redefine a class or a
functionality, we can do it without major problems. Below we will specify what each one does and what it is for (although it is quite clear based on the names):

- **androidMain**, contains Android-specific definitions and code. In this section we must initialize everything necessary for the Android platform, that is,
  Here, in the case of having to instantiate some type of object that requires a Context or any other specific Android API, it can be done here without any major problem.
- **androidTest**, contains unit test implementations that require specific Android elements. In this way, if in [androidMain] we have instantiated some
  specific object, we can test it here.
- **commonMain**, in this module is where we are going to have the bulk of implementations, as well as the expect class whose implementation is delegated to each platform.
- **commonTest**, contains unit test implementations that do not require platform-specific elements.
- **iosMain**, contains, **in Kotlin language**, specific implementations for the iOS platform, specific initializations of objects, or access to api will be implemented
  via the Kotlin-Swift bridge that implements KMP.
- **iosTest** contains unit test implementations that require specific elements or instances of the iOS platform. Is used as in
  [iosMain] the Kotlin-Swift bridge.
- **jvmMain**, this module contains specific implementations for desktop executions (whether MacOS, Linux, Windows), so any element will be used
  provided by the standard JVM.
- jvmTest, contains the implementations of unit tests that require specific elements of the JVM and that, therefore, are not of interest to the rest of the platforms.

### Definitions expect

In KMP the use of functions or classes marked with the reserved word **expect** is introduced, or becomes necessary, these functions and/or classes are defined in the module
[commonMain], but are implemented on each platform, marking their implementation with the keyword **current**. Keep in mind that, to carry out the implementation, it is required
of a particular nomenclature, for example, if in [commonMain] we have the file (defined by its package) [io.afalabarce.mvvmkmmtemplate.data.datasources.core.db.DriverFactory.kt]
On the rest of the platforms, we must define a file that will be the one in which we will implement the class (or expect function) that we require:
- **[io.afalabarce.mvvmkmmtemplate.data.datasources.core.db.DriverFactory.android.kt]**, the implementation of the class/functions is carried out using android's own elements.
- **[io.afalabarce.mvvmkmmtemplate.data.datasources.core.db.DriverFactory.ios.kt]**, the implementation of the class / functions is carried out using the Kotlin - Swift bridge provided by KMP.
- **[io.afalabarce.mvvmkmmtemplate.data.datasources.core.db.DriverFactory.jvm.kt]**, the implementation of the class / functions is carried out using elements of the standard JVM.

Let's look at an example, precisely with the file proposed as an example:

#### Definition in commonMain

```kotlin
expect class DriverFactory {
     fun createDriver(): SqlDriver
}
```

#### Implementation for Android platform

```kotlin
current class DriverFactory {
   private val context: Context by inject(Context::class.java)
   current fun createDriver(): SqlDriver {
     return AndroidSqliteDriver(
       KmmDatabase.Schema,
       this.context,
       Database.databaseName)
   }
}
```


#### Implementation for iOS platform

```kotlin
current class DriverFactory {
     current fun createDriver(): SqlDriver {
         return NativeSqliteDriver(
             KmmDatabase.Schema,
             Database.databaseName
         )
     }
}
```

#### Desktop Implementation (JVM)

```kotlin
current class DriverFactory {
     current fun createDriver(): SqlDriver {
         val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
         KmmDatabase.Schema.create(driver)
         return driver
     }
}
```

As we see in each implementation a KmmDatabase class is defined, this class is generated by the compiler, since it is that name that we have given in the configuration
gradle for SqlDelight based database.

As we can see, once we mark the class as expect, all its implementations must be marked as current, as well as the functions or properties it may contain.


## Execution of the app on the different platforms

Three project tasks have been provided for the execution of the app developed on each platform,
In this way we can save the process of launching them from the console with the corresponding tasks
from gradle:

![app_execution_tasks.png](art/app_execution_tasks.png)

Even so, they can be executed by launching the following tasks:

### Android

- Run the task `./gradlew :presentation:ui:assembleDebug`
- Find the `.apk` file in the path `presentation/ui/build/outputs/apk/debug/composeApp-debug.apk`

### Desk

- Launch the desktop application with the task `./gradlew :presentation:ui:run`

### iOS

To launch the application on an iOS emulator, we will have two possibilities:
- Open the project `iosApp/iosApp.xcproject` in XCode and run it with the standard configuration.
- Using the KMP plugin, from Android Studio itself.

## Project Architecture

The Architecture of this project is based on the classic Clean Architecture, with some particularities
which, I think, make it interesting both on a didactic and productive level, since it provides a separation
total of each layer, allowing maximum decoupling in each section. Thus, in a schematic way (without entering
in the internal structure created for KMP), we can see all the modules defined in the following image:

Taking into account the following (always thinking about Origin -> Destination):
- <span style="color:#339966">**Green Arrows**</span>, Module A is a dependency of Module B
- <span style="color:#ff0000">**Red Arrows**</span>, Module A uses functionality from Module B
- <span style="color:#ff99cc">**Pink Arrows**</span>, Module A is implemented in Module B

![MVVM KMM Template.png](art/MVVM_KMM_Template.png)

As we can see in the image, the project is divided in a "superficial" in four layers, which
in turn they will implement each module that will abstract and protect improper access from one module to another.
The functionality of each layer, as well as that of each module that composes it, is explained below:

- **core**, is a transversal layer, which has modules accessible to specific layers, for specific actions
  but at the same time, it has visible modules for all layers, which provide functionalities that
  They can be of common use. Let's look at each of the modules that make up this layer:
  - *common*, this module is visible to all layers and provides useful methods that can be used on any
    of the project layers (for example, it provides methods that tell us the system on which the app is running).
  - *viewmodels*, this module is only visible by the viewmodels module of the **presentation** layer, and provides
    Basic functionality for management and manipulation of states in viewmodels.
  - *di*, this module only collects the different dependency injection objects of each module, to provide them
    to the **UI module of the presentation layer**, in order to correctly manage the dependency tree.
  - *ui*, this module is only visible by the **ui module of the presentation layer**, both the theme and the theme are provided in this module
    from JetpackCompose, as general-purpose composables. Ideally, this layer houses abstractions of the components to be used,
    since this way, if any compose element is modified, the main UI will not be affected. For example, we can
    develop the composables of TextField, Button, PasswordTextField, etc., adapted to the theming that we require.
- **data**, this layer is responsible for managing **all** the data sources from which the app will be fed. ideally,
  It will even manage possible data acquisition from GPS, Bluetooth, etc., in addition to the classic requests to REST services,
  Local databases, preferences management, etc. Next, the functionality and responsibility of each of the
  modules that make up this layer:
  - *models*, this module maintains, ideally separated by feature, the data models of both local and remote databases.
  - *repository*, in this module **the repository interfaces defined in the domain:repository module are implemented**, it is also the
    responsible for the implementation/definition of the various mappers to/from domain entities (**domain:models**) to entities
    or remote databases.
  - *datasources*, in this module the interfaces that will manage each and every one of the requests to data sources are defined. This module
    is **referenced by the data:repository module**.
  - *datasources-core*, this module is responsible for centralizing all the implementations of the interfaces defined in **data:datasources**,
    being therefore the **only module of the entire project with the capacity to connect to external systems and/or local databases**.
- **domain**, In this layer we manage the business logic that brings together the methods of the repositories in the different use case classes, in order
  to limit and atomize requests, to use only and exclusively the functionalities necessary at a given time.
  - *models*, in this module we will define, ideally, by feature the different domain data models, which will be used by the presentation layers
    and that are provided to us already mapped from the repositories.
  - *repository*, in this module the communication interfaces that will be implemented in the **data:repository** module are defined. These interfaces are,
    ultimately, the communication contract between the data access layer and the presentation layer.
  - *usecases*, this module is responsible, organizing everything by feature, for the abstraction of the repository calls, delimiting each use case
    to a specific functionality, this means that a use case should not implement more than one functionality, for example, GetDeviceIdUseCase,
    SetDeviceIdUseCase, etc.
- **presentation**, this is the layer responsible for interaction with the user, in which we will define both the UI and the data collection system
  from the domain layer, through ViewModels.
  - *viewmodels*, this module is responsible, organized by feature, for managing the different requests and manipulation of data and states, based on the
    use cases, through the **domain:usecases** module, providing the UI with information reactively, thus ensuring that, apart from
    that the UI will not be frozen, we will have a single point of data collection and updating.
  - *ui*, this module will be in charge of presenting the information provided by the different viewmodels defined in the module
    **presentation:viewmodels**, we must pay attention that ideally **we should not use generic compose components, in favor of components
    own**, since in this way, we will be able to extract the responsibility for future changes in functionality or appearance to a single point.

At this point, we have defined what each module does in each layer, but it remains to explain / define what is exposed in the graphical diagram.
Let's see by module what its dependencies are:

- **Practically all modules** depend on core -> common, so it is not necessary to indicate this dependency.
- **domain -> repository**, requires the inclusion of the following dependencies:
  - *domain -> models*, since [domain -> repository] is where the interfaces that will provide the data obtained from the different data sources are defined
    already mapped to the domain entities defined in this layer.
- **domain -> usecases**, the following dependencies are defined in this module:
  - *domain -> models*, since [domain -> usecases] has domain entities as return values, it requires this module to be able to perform this task.
  - *domain -> repository*, the use cases defined in [domain -> usecases] require the data to be returned from the functions defined in the interfaces
    described in this module.
- **data -> datasources**, has the following dependencies:
  - *data -> models*, since it requires the data models (local and remote)
- **data -> datasources-core**, requires the following dependencies:
  - *data -> datasources*, since it is in [data -> datasources-core] where the implementation of the defined interfaces is carried out
    in this module.
  - *data -> models*, since it requires the data models (local and remote) for the implementation of the interfaces defined in the previous point.
- **data -> repository**, requires the following dependencies:
  - *data -> datasources*, since it is [data -> repository] where the interpretation of datasources results and their adaptation to domain entities is carried out.
  - *data -> models*, since it uses and is required by the results provided by the previous dependency.
  - *domain -> models*, since it is in [data -> repository] where the mapping of data entities (local and remote) to domain entities is carried out.
  - *domain -> repository*, since [data -> repository] is where the interfaces defined in this module are implemented.
- **presentation -> viewmodels**, this layer requires the following dependencies:
  - *domain -> models*, in the presentation and domain layers, we only collect data based on the domain entities defined in this module.
  - *domain -> usecases*, the viewmodels have as their only data source the results provided by the different use cases defined in this module.
  - *core -> viewmodels*, this dependency provides certain functionalities that can be used by the [presentation -> viewmodels] module.
- **presentation -> ui**, the presentation layer has the following dependencies:
  - *domain -> models*, in the presentation layer, we only collect data based on the domain entities defined in this module.
  - *presentation -> viewmodels*, the presentation layer, obtains and provides feedback to both local and remote data resources **through viewmodels only
    related**.
  - *core -> di*, since the dependency provider (Koin) must be managed from the presentation layer, the dependency tree must be passed to it aseptically, which
    we get by including this dependency and a concrete object that defines the entire tree.
  - *core -> ui*, thanks to this module, we are going to centralize all the appearance and theming of the app, to do this, it is in this module where the theme of the app will be defined, as well as the
    basic components (already with theme values applied) that are going to be used in the app.

As we can see, based on the description of the different dependencies that we have defined, we are sure that each layer is only visible by the correct layer / module
completely avoiding possible improper access, whether intentionally or mistakenly.

## Functionalities implemented (per layer)

### Core

In the core layer, some of the basic functionalities of the entire app are implemented, particularly the different elements that will allow us to manage
both the dependency injection tree and the management of Viewmodels. Let's see, for each module, the most interesting possibilities it provides us:

#### Common

This module defines one of the most important functionalities of the entire project, since it allows us to manage the entire process of providing information in a relatively simple way.
dependencies throughout the project (hence this module is a direct dependency of the rest of the project's modules).

The **KoinModuleLoader** interface is defined in the package [io.github.afalabarce.mvvmkmmtemplate.core.common.di], which exposes a read-only property
called **koinModules**, which is nothing more than a list of koin modules.
We will implement this interface in each module that requires dependency injection, for example,
[data -> datasources-core], [data -> repository], [domain -> usecases] and [presesntation -> viewmodels].

Its use is very simple, let's see it with a code example (extracted from [data -> datasources-core]):

```kotlin
object DataSourceCoreDependencyInjector: KoinModuleLoader {
     override val koinModules: List<Module>
         get() = getPlatformInjects().union(
             listOf(
                 module {
                     single <ApiService>{
                         Ktorfit
                             .Builder()
                             .baseUrl(ApiService.API_URL)
                             .build()
                             .create()
                     }
                     singleOf(::Database)
                     single<AppPreferences>{ AppPreferencesImpl(get()) }
                 }
             )
         ).toList()
}
```

As we see, the simplest thing is to create an object that implements the interface, and directly define the module with the module's own dependencies.
In addition, a getPlatformInjects method is provided that is marked as **expected** so that on each platform we define the corresponding dependencies, that is, based on a platform
or another, certain dependencies will be defined that are required by one system or another (**see examples defined in the layer**).

In addition, it has certain extension functions, which in one way or another will facilitate various operations (especially object formatting and safe conversions between types).

#### ViewModels

In this module, only one KmmViewModel class has been defined (inheriting from Viewmodel) that implements an extension method to the CoroutineScope that allows safe execution of a suspension method, since it allows us to safely manage
possible errors easily. Thus, our ViewModel should be similar to the following example:

```kotlin

class MyViewModel(
     private val myUseCase: MyUseCase
): KmmViewModel() {
     val myUseCaseResult by lazy { myUseCase.flowResult.stateIn(viewModelScope, SharingStarted.Lazily, initialValue) }
  
     fun queryResult() {
         viewModelScope.safeLaunch(
             onStart = { /* do on start */ },
             onFinish = { /* do on finish */ },
             onError = { /* do on raised error */ },
             onStartContext = Dispatchers.Main,
             onFinishContext = Dispatchers.Main
         ) {
             // Suspend query calls
         }
     }
}

```

#### Di

This module is responsible for centralizing the different dependency trees of each module, bringing them all together in one
only tree that will allow us to easily generate dependencies, also minimizing possible errors, because it is
bounded each module.

To do this, just generate an object called [io.github.afalabarce.mvvmkmmtemplate.core.di.CoreDependencyInjection] which provides,
like the rest of the dependency objects, the implementation of the interface [io.github.afalabarce.mvvmkmmtemplate.core.common.di.KoinModuleLoader].

Unlike what happens with the dependency injection of other modules, in this it is not necessary to add anything (unless we modify the architecture and add
our own modules).

For everything indicated in the previous paragraphs, **this layer does not need to be altered**.

#### Ui

This module will be responsible for centralizing the entire overview subsystem, as well as the management of the theme. For this, the theme design has been customized
of the application providing it with some interesting functionalities that the default theme provided by the JetpackCompose definition does not have. Let's see below
what are we talking about:

The module provides an AppMaterialTheme, which implements, apart from what is provided by MaterialTheme, a new property called [dimens] which stores the configuration
of dimensions for the project, therefore, these values are stored in an object of type MobileDimens, which has the following signature:

```kotlin
class MobileDimens(
    val none: Dp,
    val topSurface: Dp,
    val startSurface: Dp,
    val endSurface: Dp,
    val bottomSurface: Dp,
    val minTopSurface: Dp,
    val minStartSurface: Dp,
    val minEndSurface: Dp,
    val minBottomSurface: Dp,
    val mediumMarginTopSurface: Dp,
    val mediumMarginStartSurface: Dp,
    val mediumMarginEndSurface: Dp,
    val mediumMarginBottomSurface: Dp,
    val largeTopSurface: Dp,
    val largeStartSurface: Dp,
    val largeEndSurface: Dp,
    val largeBottomSurface: Dp,
    val appMargin: Dp,
    val textFieldCorner: Dp,
    val smallIconSize: Dp,
    val buttonHeight: Dp,
    val dialogCorner: Dp,
    val focusedBorder: Dp,
    val cornerRadius: Dp,
)
```

As we can see, most of the parameters that an app can have are covered (if we require a new parameter we will only have to add it).

Additionally, a LocalComposition-based preload, called [LocalDimension], is provided during recomposition, which, via [LocalDimension.current]
It will allow us to access the system dimensions configuration.

Some default shapes are also provided, and a couple of custom shapes, so that you can appreciate the power of compose when designing shapes.

Thanks to the elements we have, it is in the Core-UI module where we are going to define the generic components that our app is going to use, that is, we should
**prohibit** the use of composables from the general APIs in our app, defining our own wrappers, with our theme, and which would be the ones used in the system,
Thus, thanks to this, in the event that there is a change in the general APIs, we will only have to modify the dependencies and references / calls in a single
point, a possible refactor being simpler.

### Data layer

It is in this layer where we have the most implemented functionalities and perhaps the most complex to describe, for this we will try to be as explicit as possible in the explanation
of the functionalities, as well as their use/deployment.

**NOTE**: Keep in mind that, except for the [datasources-core] module, it should not be necessary to make specific implementations for each platform in the rest of the modules.

Let's start, starting with the most internal modules:

#### Models

In this module, we will define the data classes necessary for the correct management of both local and remote entities.
Keep in mind that, since we use SqlDelight as a local persistence engine, we are going to have to define our entities
locals in a slightly special way (applies the same format to entities for remote):

```kotlin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExampleEntity(
     @SerialName("id")
     val id: Long,
     @SerialName("title")
     val title: String,
     @SerialName("description")
     val description: String
)
```

As a tip, the ideal is to distribute the different data models based on their features, organizing them locally or remotely depending on the
needs.

#### Datasources

This layer is nothing more than the definition of the communication interfaces between the implementation of the repositories and the implementation of
these interfaces or contracts, taking into account that the returned types will be those defined in the module
[data -> models]. This module does not need further explanations.

#### Datasources-core

This layer is the one that, by far, has a higher level of complexity at the project level since, among others, it has the implementations and definitions of
the different data sources of the app, among which we will see them in greater detail below:

- **Ktor + Ktorfit**, as a connection system to external APIs (REST mainly.)
- **Jetpack Datastore**, as a preferences storage system.
- **SqlDelight**, such as "ORM" and data access layer.

Let's look at each of these data access systems in detail:

##### **Ktor + Ktorfit**

[Ktor-Client](https://ktor.io/docs/getting-started-ktor-client.html) is the de facto standard in managing external communications, for example REST APIs, but although it is not excessively complex its use, it is overshadowed by the
system used constantly on Android, that is, [retrofit](https://square.github.io/retrofit/). To remedy this "problem" and allow an almost flat learning curve, the developer
[Foso Github](https://github.com/Foso) has created a plugin and its corresponding libraries for KMP, called [Ktorfit](https://github.com/Foso/Ktorfit) thanks to which we can use APIs in KMP in the style of like
we use them on Android.

Below is the definition of a typical interface used with Ktorfit:

```kotlin
interface ApiService {
     @GET("/api/values")
     suspend fun getAllItems(): List<String>

     @POST("/api/values")
     suspend fun putAllItems(@Body items: List<String>)
     companion object {
         const val API_URL = "https://your.own.api"
     }
}
```

As we can see, the definition of the interface is identical to what we already know about retrofit.

To create the ApiService instance, we will rely on the dependency injector, that is, Koin:

```kotlin
expect fun getPlatformInjects(): List<Module>

object DataSourceCoreDependencyInjector: KoinModuleLoader {
    override val koinModules: List<Module>
        get() = getPlatformInjects().union(
            listOf(
                module {
                    single <ApiService>{
                        Ktorfit
                            .Builder()
                            .baseUrl(ApiService.API_URL)
                            .build()
                            .create()
                    }
                    singleOf(::Database)
                    single<AppPreferences>{ AppPreferencesImpl(get()) }
                }
            )
        ).toList()
}
```

As we see, we establish more elements, apart from the creation of the ApiService instance, which helps us, didactically,
to better understand the dependency injection system proposed in this template.


##### **Jetpack Datastore**

In the Android world, [Jetpack Datastore](https://developer.android.com/jetpack/androidx/releases/datastore) has become the
new de facto standard in preference storage, thanks to its reactive and asynchronous approach. For KMM, except initialization
of the object based on the particularities of each platform, the operation is identical to what we can already know if we come from the world
Android.

*As a teaching exercise, it is proposed to review how we instantiate the Datastore subsystem in this template*.

Saving the specific implementations for initialization, an example of managing preferences with JetpackDatastore is shown below:

- Interface defined in [io.github.afalabarce.mvvmkmmtemplate.data.features.preferences]:

```kotlin
interface AppPreferences {
     fun getDeviceId(): Flow<Long>
     suspend fun setDeviceId(deviceId: Long)
}
```

- Interface implementation, in [io.github.afalabarce.mvvmkmmtemplate.data.core.features.preferences]

```kotlin
class AppPreferencesImpl(
     private val dataStore: DataStore<Preferences>
): AppPreferences {
     override fun getDeviceId(): Flow<Long> = dataStore.data.map { preferences ->
         preferences[DEVICE_ID_KEY] ?: 0
     }

     override suspend fun setDeviceId(deviceId: Long) {
         dataStore.edit { preferences ->
             preferences[DEVICE_ID_KEY] = deviceId
         }
     }

     companion object {
         private val DEVICE_ID_KEY = longPreferencesKey("DeviceId")
     }
}
```

All the creation of preferences is managed from each platform, thanks to the method defined in
[io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.features.preferences.DataStoreInstance.kt], in conjunction with
the particular dependency injection of each platform (see the Koin code example in the Ktor section, the expected fun
defined and their corresponding current fun).

As a final note about DataStore, the interesting thing is that we already have everything defined, and we only have to focus on what is important,
define and implement the interface and its methods from which we will rely for simple management of preferences.

##### SqlDelight

This characteristic is, perhaps, one of the most important, but also the one that has the most differences with respect to app development.
Android, since although with Room everything was quite simple, with SqlDelight, although it is not complex, it is not as simple as with Room.

SqlDelight allows us to greatly abstract the [CRUD](https://es.wikipedia.org/wiki/CRUD) operations, the final result, after
compile successfully, is that a series of classes are created that will allow us to perform the different operations now, in code
Kotlin, similar to what we achieved with Room.

- **Initialization of the data model**

As in any project that requires a local database system, we must define an initial modeling that will be the basis of our
development, for this, we require the creation of a **file with a .sq extension**, its name is indifferent, although by convention it is
Ideally, name it with the same name as the project (for example), but in a very particular path of the common code section ([commonMain]):

![sqldelight_sq_file_path.png](art/sqldelight_sq_file_path.png)

As we see in the previous screenshot, a [sqldelight] folder is created at exactly the same level as the code folders, also creating a
**directory structure identical to the one we have in code**, this is very important since it is what will determine the correct creation,
at compile time, of certain objects and entities that will be useful in data mapping.

**Structure of the .sq file**

This file has a fairly simple structure, but with certain restrictions that we must take into account:

- It is a SQL code file, so each SQL statement we write must end with a ;
- SQL reserved words **must be capitalized** otherwise compilation errors will occur.

Regarding its structure, at the beginning of the file we will define all the tables and constraints (constraints) that we consider necessary for our
data model, then, and here is the most interesting part, we will define the methods that we are going to use in our queries, taking into account
that the **name of the method goes on one line, ending with :** and in the following lines, the SQL statement that will allow the data to be obtained.
As a particularity, **when defining a method, if it must have parameters, these will be indicated in the SQL statement using ?**.

Let's look at a practical example:

```sqldelight
CREATE TABLE table_example (
     id INTEGER PRIMARY KEY AUTOINCREMENT,
     title TEXT NOT NULL DEFAULT '',
     description TEXT NOT NULL DEFAULT ''
);

selectAll:
SELECT * FROM table_example;

insertItem:
INSERT INTO table_example(title,description)
VALUES (?, ?);

updateItem:
     UPDATE table_example SET title = ?, description = ? WHERE id = ?;

deleteById:
DELETE FROM table_example WHERE id = ?;

deleteAll:
DELETE FROM table_example;
```

In the example above, several things will happen after compiling:

- A Table_example class will be created that abstracts the structure of the table we have created.
- A class of type KmmDatabase will be created, which, when injected, will provide us with several interesting properties:
  - Schema, will provide us with the tables and other important elements of the database.
  - queries, of type [MvvmKmmTemplateQueries] (note the prefix that matches the Name of the .sq file) in which we are
    provide all the methods defined in the sq file.

Once successfully compiled, we can create our Dao, although as SqlDelight works, our Dao will be our
queries property of the KmmDatabase class, so, we only have to create our data bridge, let's see an example:

```kotlin
class Database(databaseFactory: DriverFactory) {
     private val database = KmmDatabase(databaseFactory.createDriver())
     private val dbQuery = database.mvvmKmmTemplateQueries

     internal fun clearDatabase() {
         dbQuery.transaction {
             dbQuery.deleteAll()
         }
     }

     internal fun deleteById(id: Long) {
         dbQuery.transaction {
             dbQuery.deleteById(id)
         }
     }

     internal fun getAllEntities(): List<ExampleEntity> {
         return dbQuery.selectAll(::exampleEntityMapper).executeAsList()
     }

     internal fun insertOrUpdateEntities(vararg entities: ExampleEntity){
         dbQuery.transaction {
             entities.forEach { entity ->
                 if (entity.id == 0L) {
                     dbQuery.insertItem(
                         entity.title,
                         entity.description
                     )
                 } else {
                     dbQuery.updateItem(
                         entity.title,
                         entity.description,
                         entity.id
                     )
                 }
             }
         }
     }

     private fun exampleEntityMapper(
         id:Long,
         title:String,
         description: String
     ): ExampleEntity = ExampleEntity(
         id = id,
         title = title,
         description = description
     )

     companion object {
         val databaseName = "KmmDatabase"
     }
}
```

As we can see, we can encapsulate (it is highly recommended) all requests in a transactional environment, thanks to [dbQuery.transaction],
This is because in case of inserting or deleting objects that are related to each other, if an error occurs
the entire transaction would be canceled and there would be no inconsistent data left in the system.

We will also look at those private "mappers", these will help us map an object that is of type Local Entity to what the system requires
As we see, we pass a reference to the mapping function and automatically, the result we need is generated.

- **Migrations**

A very important aspect in any app that has among its characteristics the local storage of information in a SQL database
It is its evolution, an evolution that may cause new needs and, therefore, require modifications in the structure of the database.

As happened in Android with Room, certain modifications and instances of elements are required that allow us to alter the database,
With SqlDelight we also have the possibility of executing migrations from one version of the database to another, to do this we must have within
the sqldelight folder of a **folder called migrations**, within which we will add each change we need to the database, yes
We must take into account that, in this case, **the extension of the files to be used will have a .sqm extension** and will be named depending on the version with
the one we are going to work on (1.sqm, 2.sqm, ...).

**ATTENTION**: the 1.sqm file indicates that we are migrating from version 1 to 2, the 2.sqm file is the one that will manage the change from version 2 to 3,
and so on.

For complete information on visiting the related site for [SqlDeLight Migrations](https://cashapp.github.io/sqldelight/2.0.0-alpha05/multiplatform_sqlite/migrations/).

#### Repository

In this module, we define both the mappers (ideally as extension functions, so as not to overload the dependency tree), and the implementations of
the interfaces defined in [domain:repository], it is here where, depending on the needs, we will determine the origin of the data to be displayed (for example, based on expiration of
cache we can determine that by refreshing a list it is time to retrieve it again from a remote API).

**As in the majority of modules, in principle it should not be necessary to carry out any implementation outside the scope of [commonMain]**.

### Domain layer

The domain layer is perhaps one of the simplest, and requires minimal explanation, although we will focus on some good practices to take into account.
Keep in mind that the domain layer is the only one that does not have external dependencies, except [core:common], because it provides some utilities.

Let's look at each module separately:

#### Models

In this module, as in the rest, **we should implement the domain data models based on feature** in this way we would maintain the logical order and understanding of the
project by "complex" development teams will be larger.

#### Repository

In this module we will define the interfaces / contracts of each feature that will provide us with access to the different data, as always, organized by feature.

#### Usecases

This module is responsible for performing an atomic separation of the functionalities provided by the repositories, organizing everything, again, by features. What does this mean?
Let's look at an example:

Let's assume the following repository:

```kotlin
interface MyCustomRepository {
     fun getPeople(): Flow<List<Person>>
     suspend fun insertOrUpdatePeople(vararg people: Person)
     suspend fun deletePeople(vararg people: Person)
}
```

To implement the use cases related to this repository, we should create three files, each one for a functionality:

- **GetPeopleUseCase.kt**

```kotlin
class GetPeopleUseCase (
     private val repository: MyCustomRepository
) {
     operator fun invoke(): Flow<List<Person>> = repository.getPeople()
}

```

- **CreateOrUpdatePeopleUseCase.kt**

```kotlin
class CreateOrUpdatePeopleUseCase (
     private val repository: MyCustomRepository
) {
   operator fun invoke(vararg people: Person) = repository.insertOrUpdatePeople(people)
}
```

- **DeletePeopleUseCase.kt**

```kotlin
class DeletePeopleUseCase (
     private val repository: MyCustomRepository
) {
   operator fun invoke(vararg people: Person) = repository.deletePeople(people)
}
```

### Presentation layer

The presentation layer is where everything explained in the previous layers will make sense, since it is in this layer where, thanks to the data provided
Through the domain layer, we will be able to represent the information that the user needs in the UI that we will provide. Let's keep in mind that this template is
It is based on an MVVM pattern, so in this layer we will define the following modules (although due to the architecture provided, the migration to an MVI pattern should not be complex)
with its own peculiarities:

#### ViewModels

This module is the link between the domain layer and the UI, being responsible for capturing the data provided by the data layers, and requesting updates/actions
based on requests that will be launched thanks to user interactions. This layer is totally independent of the UI layer, which is logical if we want our project to be
testable. All our Viewmodels must inherit from [KmmViewModel], which, as we saw in the definition of [core:viewmodels] provides a series of functionalities that will facilitate the
secure execution of requests to use cases. Let's remember the example that was indicated in the description of [core:viewmodels]:

```kotlin
class MyViewModel(
     private val myUseCase: MyUseCase
): KmmViewModel() {
     val myUseCaseResult by lazy { myUseCase.flowResult.stateIn(viewModelScope, SharingStarted.Lazily, initialValue) }
  
     fun queryResult() {
         viewModelScope.safeLaunch(
             onStart = { /* do on start */ },
             onFinish = { /* do on finish */ },
             onError = { /* do on raised error */ },
             onStartContext = Dispatchers.Main,
             onFinishContext = Dispatchers.Main
         ) {
             // Suspend query calls
         }
     }
}
```

As we can see in the creation of the read-only myUseCaseResult property, we generate a StateFlow, lazily, and with an initial value (remember, the Flow, require an initial value),
This way, we have everything ready for reactive data painting in our UI. The most interesting thing about this ViewModel is the call to the safeLaunch extension function, which will allow us to
a fairly clean way, the execution, state control in the time flow, and of course, error control, of our function calls.

#### UI

Finally, we have the module with which the user will interact with our application. If we look back, we will see that everything explained above comes together so that the development of the UI remains
as centralized as possible.

Keep in mind that, in order to make our application scalable, and above all, **independent of future changes in compose components**, we must follow the guideline explained in [core-ui]
regarding the use of generic Composables. As a rule, **in this module we will not use composables from the APIs provided by Google**, but we will use our own parsers that will be
defined, as we have already mentioned at the beginning of this README, in the [core-ui] module. This has a very important advantage, and that is that, whether we want to modify the appearance, or what we are going to do
adapt the components that we already have deployed to a hypothetical new version of composables, we would only have to make the change in a single point, this module not being affected by those changes.

Some of the few differences we have in developing in KMP with Compose Multiplatform compared to developing Android apps are the following:

- We do not have ConstraintLayout (yet, 01/07/2024) officially, instead, the dependency is added for an experimental implementation of
  [Samuel Gagarin](https://github.com/Lavmee/constraintlayout-compose-multiplatform) who provides us with ConstraintLayout.
- Since we do not have ViewModels as such, and our implementation of viewmodels is from the development of [MokoMVVM](https://github.com/icerockdev/moko-mvvm), to inject
  the viewmodels in our composables, we will use koinInject and not koinViewModel, just as we did in Android.

## Deployment

As we well know, each platform is particular in terms of its deployment system, so the template has been prepared for deployment on each of them in a specific way.
relatively simple. So, we have the following deployment configurations, per platform, all parameterized, of course in the [presentation:ui] module:
- Android, the deployment will be managed, also indicating the generation of an aab package (for publication to the Play Store) using an encryption key store, configured in the
  build.gradle.kts from the presentation module.
- iOS, this deployment depends largely on the equipment configuration that we have made, preferably from XCode, since it will leave everything ready for its generation.
- JVM, this is perhaps the most "curious" (not complex) since it allows us to generate (depending on the operating system in which we compile), a suitable package for each platform, so
  we can generate packages for:
  - macOS, we generate a .dmg file that will facilitate the deployment task.
  - Windows, we generate an .msi file.
  - Linux (Debian), we generate a .deb file.

To perform the deployment (generate the installers for the desktop platforms), we just have to move to the gradle task tree and,
In the list of project tasks, look at the one that indicates [compose desktop], in it we will see the different tasks available
**packageRelease[Deb|Msi|Dmg]**

In addition, a gradle task called deployKmmProject has been added that allows us, in an extremely simple way, to modify the package,
applicationId, directory structure, etc. automatically. To do this, just run the following command from the console to obtain
some help on its use:

```bash
# ./gradlew -q deployKmmProject 
Project Deploy Task Help
-------------------------------
This task can relocate packages and folders from source package to destination deploy package
- sourcePackage, Source package from template, by default io.github.afalabarce.mvvmkmmtemplate
- deployPackage, Deploy package for the template, can't be empty, and not equals to sourcePackage

Usage:
./gradlew -q deployKmmProject --sourcePackage="io.github.afalabarce.mvvmkmmtemplate" --deployPackage="io.github.afalabarce.awesomeapp"

```