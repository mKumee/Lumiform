# Lumiform Android Code Challenge
Builded an android app that fetch a json and map all the data structured and organised as page,sections,question tree with 
single/multiple choice selection.
## Setup
After oppening the project on Android studio and choosing app module to run the project nothing else needed.

## On This project is managed
-Custom Theme Night/Light mode
-Json parsed/mappers
-Network manage including( Offline cache saving last data, refetch/retry network requests, Network states handle)
-shared preference ( used to save selected Theme, and selected answers of questions)
-Database manage for supporting offline saved data

## Offline logic
-Network request data is cached to room database on every Successful state and on Failure is shown last datas saved on cache, 
instead of an blank/error screen no network 

## UI
Everything is jetpack compose, no xml. one long scrollable list shows pages, sections and
questions together, with the current page pinned at the top while you scroll and it swaps
to the next page once you scroll past it.

sections can be collapsed by tapping them, wasn't asked for but a document with a lot of
sections gets hard to read if everything's always open.

font size gets smaller the deeper you go (page, then section, then nested section, then
question) so you can tell where you are just by looking at the text size, without needing
any color coding.

theme is light/dark only, no "follow system" option, and no material you dynamic color
either, wanted the app to keep the same look no matter the phone's wallpaper.

## Tests
" ./gradlew test " running the command on terminal will run all the jvm. Room dao use robolectric, and the other part use mock data.

## Modules
Project is organized on multimodule for better mantain,, easy feature added and easy testing separate logic.
In the future we can use specific module as external also if needed on this way
The current organisation of the project:
- App Module: DI, Main Activity, Main Navigation manage
- core:models (json necessary classes domain)
- core:common ( States manage: Network Request, Ui States, Data Result, DI Dispatcher)
- core:network - (retrofit and DTOs)
- core:database - (Room db, build tables and all queries needed)
- core:data - (repository,mappers,datastore/sharedpreference)
- core:uicomponents - (theme,composables components that can be used ollover the project, no hilt/nav/viewmodel deps)
Feature module will be separate and will be use for every part/screen/feature separately
-feature:maincontent - main/home screen, viewmodel, nav graph 
- feature:imageDetail - full screen image

## Notes
Hilt is only added on app and feature:maincontent, the other modules don't need it since
nothing there gets injected.

For the json, I used one simple data class instead of splitting page/section/text/image/
choice into separate classes. They all come from the same "type" field anyway, so a plain
data class with a when(type) in the mapper was enough, no need for a custom serializer.

For offline, it always tries the network first. If it works, it saves everything to room.
If it fails, it shows whatever was saved last time instead of an empty/error screen.

For the choice questions, I made one usecase (ToggleResponseSelectionUseCase) since that's
the only part with actual logic to test (single vs multiple selection). I had two more
usecases before but they were just calling the repository directly with no logic, so I
removed them and the viewmodel calls the repository directly now.

## What's not here yet
things i'd add if this was going further than a challenge:
- pagination, wasn't needed for this size of json but would matter on a much bigger document
- real room migrations instead of fallbackToDestructiveMigration
- screenshot tests for the compose screens
- a ci pipeline running the tests automatically on every push
- ...
