Rules for "Ground Taxi Network Tool"

Starts:
[<Type>;][<Runway>;]<Name>;<Route>[;<Speed Default 20>]


Exit; <LU Runway>[,...]; <Exit Runway>-<LEFT/RIGHT>[,...]; <Taxiway Name>; <Route>[;<Speed Default 20>]
Exit; 15; 33; E1; HP_E1-EXP_E1
LU; <LU Runway>; <Exit Runway>; <Intersection Name>; <Route>[;<Speed Default 20>]
LU; 33; E1; HP E1-END
Stand; <Name>; <Route>[; Taxiout? 0/1 Default: 0>][;<Speed Default 20>]
Stand; 81; 81-Y1
Gate; <Name>; <Route>[; Taxiout? 0/1 Default: 0>][;<Speed Default 20>]
Gate; 01A; 01A-Z1O-Z1-Z1B
Taxiway
<Name>; <Route>[; <Has Gates/Stands? 0/1 Default: 0>][;<Speed Default 20>]
Y3; G-D1
Taxiout; <Name>; <Route>[;<Speed Default 20>]
Taxiout; 44; 44-Z7

Spaces after and before the ; will be ignored.
Example
; 33 ; => "33" not " 33 "

Routes are split by "-".
For every Stand number and "END" will be ignored for the "no other point found" check, because they are endpoints

For Routes with only one "-" the first will be the start (or the path coordinates) and the second the end (or the path coordinates).

Every Routes with more than 2 points needs to have a matching number of path points.
Non crossing points shall be marked with "_".

Gate/Stand Routes can be started from the Gate/Stand or the Taxiway, the Tool, will put the path in the correct order.
Gate/Stand Routes only need the stand number and the Taxiway names in there routes, the Toll will automaticly add the "tayiway points" to the taxiway paths.
Stands and Gates automaticly get an S or G as prefix

The default speed set for taxiways is 30 knots.

Diffent Taxi routes need to have the same names for the meeting points, so the tool can replace path points with one for the meeting points so the routes connect.

Taxiways dont need a type.

LU starts at the holding point and end on the runway with "END" at the path.
Exits will automaticly build from a lineups and the exit path behind the holding points.
Taxiways start at the meeting point with the Exit point.
The Exit path will be added to exits and to the taxiway.