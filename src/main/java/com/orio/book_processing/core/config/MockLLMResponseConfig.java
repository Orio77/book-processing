package com.orio.book_processing.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides development/test mock LLM payload beans used when running the app
 * without live model calls.
 */
@Configuration
public class MockLLMResponseConfig {

    @Bean(name = "extractedIdeas")
    public String extractedIdeas() {
        return """
                {
                    "ideaContainers": [
                        {
                            "ideaTitle": "Truthful responses enable respectful resolution in ambiguous social situations",
                            "arguments": [
                                "In uncertain interactions there is often a choice between polite fabrication and honest explanation",
                                "Direct truth may momentarily hurt but helps others understand reality",
                                "Honesty prevents hidden consequences caused by socially convenient deception",
                                "Truthful clarity can restore mutual understanding even after initial disappointment"
                            ],
                            "ideaSentencesIds": [
                                26,
                                27,
                                28,
                                30,
                                32,
                                33,
                                35,
                                36,
                                37,
                                38,
                                39,
                                50,
                                51,
                                52,
                                53
                            ]
                        },
                        {
                            "ideaTitle": "Self observation exposes habitual dishonesty and motivates commitment to truth",
                            "arguments": [
                                "Inner monitoring can reveal that speech is often driven by status seeking motives",
                                "Language may be used to manipulate outcomes rather than describe reality",
                                "Recognition of falseness encourages practice of only saying what inner judgment accepts",
                                "Developing truthful speech becomes a practical guide when uncertain about action"
                            ],
                            "ideaSentencesIds": [
                                40,
                                41,
                                43,
                                44,
                                45,
                                46,
                                48,
                                49
                            ]
                        },
                        {
                            "ideaTitle": "Honesty is essential for building trust with highly suspicious individuals",
                            "arguments": [
                                "Paranoid people closely monitor cues and detect mixed motives",
                                "Deceit undermines their willingness to engage",
                                "Careful listening combined with transparent responses promotes trust",
                                "Trust allows understanding that would otherwise be impossible"
                            ],
                            "ideaSentencesIds": [
                                54,
                                56,
                                57,
                                58,
                                59,
                                60,
                                61,
                                68,
                                70,
                                71,
                                75,
                                76,
                                97,
                                98
                            ]
                        },
                        {
                            "ideaTitle": "Truthful refusal in risky interactions can strengthen relationships",
                            "arguments": [
                                "Honest boundaries help avoid enabling harmful behavior",
                                "Direct explanation communicates respect rather than superiority",
                                "Authentic speech withstands scrutiny for hidden contempt",
                                "Truthful interaction can improve mutual respect across social differences"
                            ],
                            "ideaSentencesIds": [
                                124,
                                126,
                                132,
                                133,
                                138,
                                139,
                                140,
                                143,
                                144,
                                146,
                                147,
                                148,
                                149
                            ]
                        },
                        {
                            "ideaTitle": "Manipulative speech creates life lies that distort existence",
                            "arguments": [
                                "Words can be used politically to obtain desired outcomes",
                                "Speech aimed at pleasing authority falsifies authentic thought",
                                "Life lies attempt to reshape reality toward narrow goals",
                                "Such thinking assumes current knowledge fully defines future good"
                            ],
                            "ideaSentencesIds": [
                                153,
                                154,
                                157,
                                158,
                                159,
                                161,
                                162,
                                163,
                                165,
                                166,
                                169
                            ]
                        },
                        {
                            "ideaTitle": "Avoidance and passive conformity remove meaning from life",
                            "arguments": [
                                "Conflict avoidance prevents individuals from asserting needs",
                                "Silence enables exploitation by others",
                                "Self obliteration eliminates meaningful engagement",
                                "Hiding does not protect from suffering or decline"
                            ],
                            "ideaSentencesIds": [
                                204,
                                205,
                                207,
                                212,
                                213,
                                214,
                                221,
                                222,
                                223
                            ]
                        },
                        {
                            "ideaTitle": "Failure to reveal oneself prevents personal development",
                            "arguments": [
                                "Concealing identity blocks self understanding",
                                "Exploration forces latent capacities to emerge",
                                "New experiences trigger neurological growth",
                                "Remaining static leaves individuals incomplete"
                            ],
                            "ideaSentencesIds": [
                                224,
                                226,
                                228,
                                231,
                                232,
                                233,
                                234,
                                235,
                                236
                            ]
                        },
                        {
                            "ideaTitle": "Self betrayal weakens character and increases vulnerability to wrongdoing",
                            "arguments": [
                                "Repeated agreement against better judgment reshapes identity",
                                "Lying undermines moral strength",
                                "Weak character cannot withstand adversity",
                                "Unchallenged falsehoods eventually lead to harmful actions"
                            ],
                            "ideaSentencesIds": [
                                237,
                                238,
                                241,
                                242,
                                243,
                                244
                            ]
                        },
                        {
                            "ideaTitle": "Individual deceit contributes to institutional and societal corruption",
                            "arguments": [
                                "Denial of personal experience supports oppressive systems",
                                "Thinkers across psychology linked untruth with pathology",
                                "Personal bad faith can enable large scale harm",
                                "Corruption of individuals feeds corruption of the state"
                            ],
                            "ideaSentencesIds": [
                                334,
                                335,
                                340,
                                341,
                                342,
                                345,
                                346,
                                468,
                                469,
                                470,
                                473
                            ]
                        },
                        {
                            "ideaTitle": "Accumulated lies lead to breakdown of relationship with reality",
                            "arguments": [
                                "Small distortions combine into larger deceptions",
                                "Lies contaminate broader systems of action",
                                "Growing deception produces failure and frustration",
                                "Collapse occurs when reality is no longer trusted"
                            ],
                            "ideaSentencesIds": [
                                676,
                                677,
                                678,
                                680,
                                681,
                                687,
                                688,
                                690,
                                701,
                                702
                            ]
                        },
                        {
                            "ideaTitle": "Living truthfully allows goals and values to evolve",
                            "arguments": [
                                "Ambitions focused on character are more stable than status",
                                "Clear articulation of experience improves progress",
                                "Honesty allows feedback from reality to reshape aims",
                                "Values adjust through experience when truth is admitted"
                            ],
                            "ideaSentencesIds": [
                                558,
                                559,
                                561,
                                565,
                                566,
                                567,
                                578,
                                579,
                                590,
                                591,
                                592
                            ]
                        },
                        {
                            "ideaTitle": "Personal truth telling enables individuation despite conflict",
                            "arguments": [
                                "Following imposed goals causes disengagement",
                                "Conflict may be required to align with authentic aims",
                                "Assuming responsibility fosters maturity",
                                "Truth based conflict can benefit relationships long term"
                            ],
                            "ideaSentencesIds": [
                                593,
                                594,
                                603,
                                604,
                                605,
                                608,
                                610,
                                611,
                                613,
                                615,
                                617,
                                620,
                                622
                            ]
                        },
                        {
                            "ideaTitle": "Honesty fosters resilience against inevitable tragedy",
                            "arguments": [
                                "Illness and catastrophe are unavoidable",
                                "Mutual trust supports recovery after hardship",
                                "Adaptation requires acknowledgment of reality",
                                "Deception amplifies suffering beyond what events require"
                            ],
                            "ideaSentencesIds": [
                                359,
                                360,
                                364,
                                370,
                                372,
                                373,
                                377,
                                378
                            ]
                        },
                        {
                            "ideaTitle": "Truthful speech stabilizes cooperation and long term prosperity",
                            "arguments": [
                                "Truth creates durable social structures",
                                "Reliable speech enables partnership",
                                "Accurate past understanding guides future decisions",
                                "Truth acts as a foundational resource for societies"
                            ],
                            "ideaSentencesIds": [
                                718,
                                719,
                                720,
                                721,
                                722,
                                723
                            ]
                        },
                        {
                            "ideaTitle": "Small lies can escalate into bitterness and desire for revenge",
                            "arguments": [
                                "Minor deception initiates broader falsehoods",
                                "Distorted thinking compounds shame",
                                "Persistent failure produces resentment",
                                "Perceived persecution fuels revenge fantasies"
                            ],
                            "ideaSentencesIds": [
                                657,
                                658,
                                689,
                                709,
                                710,
                                711,
                                712
                            ]
                        },
                        {
                            "ideaTitle": "Living in truth can function as a guiding meta goal",
                            "arguments": [
                                "Specific ambitions are necessary to structure life",
                                "Goals should remain subordinate to truthful engagement",
                                "Clear criteria enable evaluation of progress",
                                "Truthful articulation combines ambition with adaptability"
                            ],
                            "ideaSentencesIds": [
                                640,
                                641,
                                642,
                                643,
                                644,
                                645,
                                646
                            ]
                        }
                    ]
                }
                                """;
    }

    @Bean(name = "explanationChatResponse")
    public String explanationAIChatResponse() {
        return """
                Here is a **clear, thorough, plain‑English explanation** of the fragment you quoted — *specifically the part your reader didn’t understand* — and how it fits into the logic of the chapter.

                ***

                # ✅ **What the Fragment Means, in Simple Terms**

                The key paragraph the reader struggled with is this one:

                > *“I had had a strange set of experiences… I found myself subject to some rather violent compulsions… and developed the conviction… that I really knew rather little about who I was and what I was up to… I soon divided myself into two parts: one that spoke, and one, more detached, that paid attention and judged… I started to practise telling the truth—or, at least, not lying… The experience was disconcerting, to say the least.”*

                Let’s break it down.

                ***

                # 🔍 **1. He experienced impulses that scared him**

                Jordan Peterson says he went through a period where **violent thoughts or impulses “popped up” in his mind**.
                He stresses **he didn’t act on them**, but their intensity was disturbing enough that he thought:

                *   “Why would I even think something like that?”
                *   “Do I actually know what’s going on in my own mind?”
                *   “Who am I, if such impulses exist in me?”

                This is **not unnatural** — many psychologists note that intrusive, unwanted impulses occur in healthy people. But for Peterson, it became an existential wake‑up call.

                This leads to…

                ***

                # 🔍 **2. He realized he didn’t understand himself nearly as well as he thought**

                These impulses revealed that:

                *   his **self‑knowledge was incomplete**
                *   he had **layers of motivation and thought** outside conscious awareness
                *   he needed to **watch himself more closely** instead of assuming he understood his own intentions

                This is a core theme in psychology:
                **People lie to themselves constantly, usually without realizing it.**

                ***

                # 🔍 **3. So he started closely observing everything he said and did**

                He decided:

                > “I will observe myself as if I were another person.”

                That’s what he means by saying he “divided himself into two parts”:

                ### ✔ **Part 1: The one who speaks**

                The normal, everyday “Jordan” — talking, interacting, reacting.

                ### ✔ **Part 2: The observer**

                A kind of internal judge or witness who watches what the speaking part says and evaluates:

                *   Is that actually true?
                *   Is that really what I think?
                *   Am I saying this to look good?
                *   Am I manipulating?
                *   Am I hiding something?

                This “division” is not schizophrenia.
                It’s the same psychological mechanism used in:

                *   meditation
                *   cognitive therapy
                *   metacognition (“thinking about thinking”)

                It’s a form of **self‑supervision**.

                ***

                # 🔍 **4. He discovered most of what he said was not genuinely true**

                When he observed himself honestly, he realized:

                > “Almost everything I said was untrue.”

                Not “lying” in the malicious sense.
                But:

                *   speaking to impress others
                *   exaggerating
                *   shading facts
                *   trying to win arguments
                *   hiding discomfort
                *   avoiding conflict
                *   speaking ideologically instead of authentically
                *   smoothing things over to be liked

                He was using language as a tool to **shape reality to his advantage**, rather than to convey truth.

                This is why it disturbed him:
                He saw that his words were often **strategies**, not truths.

                ***

                # 🔍 **5. So he began practicing telling the truth (or at least, not lying)**

                He decided to **stop saying things that made his “inner observer” object**.

                If a statement felt false, manipulative, or self‑serving, he would not say it.

                This:

                *   made his speech much simpler, direct, and honest
                *   forced him to confront difficult truths instead of hiding behind words
                *   removed the ego games from his interactions
                *   gave him clarity when dealing with morally ambiguous or stressful situations
                *   helped him navigate situations where no rules or guidance existed
                    (like the one with the hospital patient)

                This practice is the seed of **Rule 8: Tell the truth — or, at least, don’t lie.**

                ***

                # 🔍 **6. Why was this “disconcerting”?**

                Because—

                ### ✔ It revealed that *he was not who he thought he was*

                His identity had been partly built on distortions and small self‑deceptions.

                ### ✔ It stripped away comfortable illusions

                Telling the truth forced him to face uncomfortable realities—inside himself and in the world.

                ### ✔ It made interactions riskier

                Truth is more dangerous than polite lies.

                ***

                # 🔍 **7. How This Connects to the Chapter’s Main Theme**

                The whole chapter argues:

                *   Lying poisons individuals and societies
                *   Truth keeps chaos manageable
                *   Self‑deception makes people vulnerable to tyranny, resentment, and corruption

                This personal story is the *origin* of Peterson’s philosophy about truth:

                He learned through direct experience that:

                *   he was lying much more than he realized
                *   lies made him weaker, less self‑aware, and less capable
                *   telling the truth made him stronger, clearer, and more grounded
                *   truth is the only way to navigate uncertain situations ethically and safely

                This internal transformation explains why, when faced with the schizophrenic woman at the hospital, he chose **truth over a comforting lie**, even though it felt harsher.

                ***

                # 🎯 **In One Sentence**

                Peterson is describing a personal awakening:
                he realized he didn’t understand himself, so he began observing his own speech with ruthless honesty, discovered how often he lied without noticing, and started practicing truthfulness as a way to strengthen his character and navigate life authentically.

                ***

                If you'd like, I can also:

                ✅ Summarize the entire chapter in simple language
                ✅ Explain the psychology behind "dividing oneself into two parts"
                ✅ Tie this section to Peterson’s broader philosophy
                ✅ Explain why intrusive violent impulses are psychologically normal

                Just tell me what you want!

                """;
    }

    @Bean(name = "queryChatResponse")
    public String queryChatResponse() {
        return """
                Absolutely—yes. Many famous psychologists and philosophers have argued **exactly** what Peterson describes in that passage: the experience of *an observing part of yourself watching and evaluating the speaking/acting part*. This idea is actually one of the most influential themes in psychology, philosophy, and even contemplative traditions.

                Below is a clear list of major thinkers who would strongly agree that this “inner division” is real, important, and necessary to acknowledge.

                ***

                # 🧠 Psychologists Who Would Agree

                ## **1. William James (Father of American Psychology)**

                James distinguished between:

                *   the **“I”** — the observing self, the knower
                *   the **“Me”** — the self as object, the one being observed

                This is *exactly* the division Peterson describes. James explicitly said awareness seems split: one part acts or speaks, while another watches and judges.

                ***

                ## **2. Sigmund Freud**

                Freud’s model of the mind:

                *   **Id** — impulses
                *   **Ego** — conscious self
                *   **Superego** — internal observer, critic, moral evaluator

                The Superego is essentially the detached “watcher” that evaluates what “the one who speaks” is doing.

                ***

                ## **3. Carl Gustav Jung**

                Jung emphasized:

                *   the **Ego** (our conscious identity)
                *   the **Self** (larger, observing, regulating totality)

                Jung also described *“the observing Self”* that notices when the Ego lies, distorts, or hides truth. He directly taught that individuation requires this internal observer.

                ***

                ## **4. Alfred Adler**

                Peterson cites Adler directly in your text (re: “life-lies”), and Adler would agree that:

                *   People avoid truth by constructing useful fictions.
                *   A more honest internal observer is needed to correct one's own distortions.

                ***

                ## **5. Viktor Frankl**

                The founder of logotherapy believed humans have:

                *   the ability to *stand at a distance from themselves*
                *   observe their impulses
                *   choose their attitude

                Frankl explicitly wrote about **“self-transcendence”**, which requires an observing, evaluating self.

                ***

                ## **6. Modern Cognitive-Behavioral Psychologists**

                CBT talks about:

                *   **“metacognition”** = thinking about your thinking
                *   observing automatic thoughts
                *   evaluating whether your internal narratives are distorted

                This is practically the same as having an inner judge/observer.

                ***

                # 🧠 Philosophers Who Would Agree

                ## **1. Søren Kierkegaard**

                Peterson references him too.
                Kierkegaard believed that living authentically requires:

                *   observing oneself
                *   confronting one’s own self-deception
                *   splitting into “the self” and “the observer of the self”

                He directly said that the self is “a relation which relates to itself.”

                ***

                ## **2. Immanuel Kant**

                Kant emphasized:

                *   **apperception** — the mind's self-awareness of its own states
                *   the unity of consciousness is possible because a part of the mind *observes the other parts*

                ***

                ## **3. Friedrich Nietzsche**

                Nietzsche argued people lie to themselves constantly and must cultivate:

                *   the “hard, cold eye”
                *   the internal watcher capable of seeing one’s own motives honestly

                He considered inner self-observation a requirement for authenticity and strength.

                ***

                ## **4. Ancient Stoics (Marcus Aurelius, Epictetus, Seneca)**

                Stoics emphasize:

                *   observing your impressions
                *   separating impulse from judgment
                *   watching your mind with detachment

                This is structurally identical to Peterson’s “one part that speaks and one part that observes.”

                ***

                # 🧘♂️ Contemplative Traditions Also Agree

                While not psychologists in the modern sense, Eastern traditions have taught this for millennia:

                *   **Buddhism:** “mindfulness” = watching thoughts and speech as if you were not identical to them
                *   **Hindu Vedanta:** the distinction between the *Atman* (witness) and the mind’s actions
                *   **Gurdjieff’s Fourth Way:** the concept of “self-observation”

                These traditions consider the inner observer not only real but essential for psychological growth.

                ***

                # ✅ Summary

                **Yes, the idea Peterson describes—dividing oneself into a speaking part and an observing, judging part—is widely recognized and supported by many major psychologists and philosophers.**

                A short list of strong supporters:

                ### **Psychologists**

                *   William James
                *   Sigmund Freud
                *   Carl Jung
                *   Alfred Adler
                *   Viktor Frankl
                *   Modern CBT theorists (Beck, metacognitive psychologists)

                ### **Philosophers**

                *   Kierkegaard
                *   Nietzsche
                *   Kant
                *   The Stoics

                All of them, in different ways, argue that the ability to *observe oneself*, judge one’s own speech and actions, and detect self-deception is essential for mental health, authenticity, maturity, and freedom.

                ***

                If you want, I can also:

                *   Connect Peterson’s view directly to Jung or Adler in detail
                *   Provide short quotes (paraphrased to avoid copyright issues)
                *   Explain whether modern neuroscience supports this “inner division”

                Would you like any of that?

                """;
    }

}
