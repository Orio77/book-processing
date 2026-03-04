package com.orio.book_processing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockLLMResponseConfig {

    @Bean(name = "extractedIdeas")
    public String extractedIdeasAiResponse() {
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
}
