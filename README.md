# clojugator-en

Produces all forms of all English verbs, including irregular verbs, **in under 90 lines**. It is a very (the most?) concise rule-based conjugator.

## Usage

The `conjugate` function returns a map with the following keys:

- Non-tensed forms
	- **plain** (also known as the *lemma*)
	- **ing** (also known as the *gerund*)
	- **pp** (past participle)
- Non-past forms
	- **pres** (the general present-tense form)
	- **i** (the 1st-person present-tense form)
	- **s** (the 3rd-person present-tense form)
- Past forms
	- **past** (the simple past-tense form)
	- **i-past** (the 1st-person past-tense form)
	- **s-past** (the 3rd-person past-tense form)
	
Note that, other than for the verb **to be**, many of these will end up being the same, owing to the limited inflection of English verbs. The data is presented this way for thoroughness and symmetry.

```clojure
user=> (require 'clojugator-en.core)

user=> (clojugator-en.core/conjugate "be")
{:i-past "was", :pres "are", :s-past "was", :ing "being", :pp "been", :past "were", :s "is", :plain "be", :i "am"}

user=> ((clojugator-en.core/conjugate "be") :pp)
"been"

```

## License

Copyright © 2017 Justin Douglas

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
