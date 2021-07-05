# Bookmark Test

## Functionsweise
* CTRL+SHIFT+Ü löst ein Event aus (ToggleBookmarkHandler).

## Config Format
* ratingGroups (Array)
    * shortName (String)
    * displayName (String)
    * penaltyLimit (double): Limits the max penalty calculated for mistakes in that rating group in sum. 
	                Deactivate that feature by not providing a penaltyLimit
* mistakeTypes (Array)
    * shortName (String)
    * button (String)
    * message (String)
    * penaltyRule (Object): Defines how the penalty is calculated for this mistake. 
                   PenaltyRule type implementations have to be done in the core plugin. EXAMPLE:
        * shortName (String): ID
        * threshold (integer)
        * penalty (double)
    * appliesTo (String): shortName of the RatingGroup this mistakeType belongs to.