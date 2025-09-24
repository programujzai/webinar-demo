[] Phase 1:
    [] 1: Fetch description from TODOIST MCP to the task $1 name.
    [] 2: Analyse this specification and understand what is the new functionality about.

[] Phase 2:
Verify which parts of the system is affected by new functionality.
@CLAUDE.md might be supportive.

[] Phase 3:
Ask me questions!
Try to spot missing parts in the draft specification.

Don't assume anything feel free to ask me anything like:
- how it should behave on the frontend (how user should interact with it)
- if we need some validation, or optional fields,
- if we have to adjust some models,
- if we would prefer to adjust existing endpoints or create new ones,

[] Phase 4:
Generate a complete specification for the new functionality  following the template and save it in `AI_DOCS/specification.md`.

Specification of the template:
```markdown

## High-Level Objective  
- Creating basic structure for a new feature  
  
## Mid-Level Objective  
- Create feature package  
- Create dummy screen  
- Create dummy screen model  
- Create di module  
- Include the module in the koin graph  
  
## Implementation Notes  
- [Important technical details - what are the important technical details?]  
- [Dependencies and requirements - what are the dependencies and requirements?]  
- [Coding standards to follow - what are the coding standards to follow?]  
- [Other technical guidance - what are other technical guidance?]  
  
## Context  
  
### Beginning context  
  
- composeApp/src/commonMain/kotlin/com/cardiapath/app/InitKoin.kt  
  
### Ending context  
  
- composeApp/src/commonMain/kotlin/com/cardiapath/app/InitKoin.kt  
- composeApp/src/commonMain/kotlin/com/cardiapath/app/feature/[featurename]/di/[featurename]  
  Module.kt]  
- composeApp/src/commonMain/kotlin/com/cardiapath/app/feature/[featurename]/[featurename]Screen.kt]  
- composeApp/src/commonMain/kotlin/com/cardiapath/app/feature/[featurename]/[featurename]  
  ScreenModel.kt]  
  
## Low-Level Tasks  
> Ordered from start to finish  
1. Ask for the feature name  
```aider  
Ask for the feature name to create, do nothing beyond that. Proceed only if you have the feature name.  
Follow the naming pattern for feature packages, classes or fields. Names in [..] are placeholders having the naming pattern.  
Example:  
[JohnDoe]Screen -> JohnDoeScreen  
```  

1. Create basic structure for a new feature
```aider  
CREATE feature/[featurename]:  
    CREATE [FeatureName]Screen.kt:        ADD object [FeatureName]Screen: Screen with composable content ScreenPlaceholder("[FeatureName]"),    CREATE [FeatureName]ScreenModel.kt:        ADD class [FeatureName]ScreenModel(logger: Logger, val dispatchers: AppDispatchers): AppStateScreenModel<State>(State(), logger),        ADD inner data class State(val foo: String = "")    CREATE di/[FeatureName]Module.kt:        ADD val [featureName]Module = module { factory { [FeatureName]ScreenModel(dispatchers = get())} },        USE getLoggerWithTag("[FeatureName]Module") to inject logger    CREATE package designs```  
2. Initialize screen model on screen  
```aider  
    MODIFY fun Content() inside [FeatureName]Screen:        ADD val screenModel = getScreenModel<[FeatureName]ScreenModel>()```  
  
3. Add feature module to koin graph  
```aider  
    INCLUDE [featureName]Module in featuresModule ONLY if new feature files created.```

```