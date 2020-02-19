<template>
    <div>
        <div class="text-center" style="margin-top: 56px">
            <v-btn large @click="showCreateDialog = true" color="primary">
                Share tweet
                <v-icon right dark>mdi-pencil</v-icon>
            </v-btn>
        </div>
        <FeedMessage :deleteTweet="deleteTweet"
                     :key="tweet.id"
                     :tweet="tweet"
                     v-for="tweet in sortedTweets"/>
        <v-dialog max-width="500px" persistent v-model="showCreateDialog">
            <v-card>
                <v-card-title>
                    <span class="headline">Create tweet</span>
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-text-field
                                hint="Enter tweet"
                                label="Tweet text"
                                persistent-hint
                                required
                                v-model="tweetDraft"/>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn @click="publishTweet" color="blue darken-1" text>Publish</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
    import FeedMessage from "./FeedMessage.vue";

    export default {
        name: "FeedGrid",
        components: {FeedMessage},
        props: ['tweets'],
        data: () => ({
            tweetDraft: '',
            showCreateDialog: false
        }),
        computed: {
            sortedTweets() {
                return this.tweets.sort((first, another) => new Date(another.modified) > new Date(first.modified) ? 1 : -1)
            }
        },
        methods: {
            deleteTweet(tweet) {
                this.$resource('/tweets{/id}').remove({id: tweet.id}).then(result => {
                    if (result.ok) {
                        this.tweets.splice(this.tweets.indexOf(tweet), 1)
                    }
                })
            },
            publishTweet() {
                if (this.tweetDraft) {
                    this.$resource('/tweets').save({tweet: this.tweetDraft}).then(result => {
                        if (result.ok) {
                            result.json().then(parsedTweet => {
                                this.tweets.push(parsedTweet)
                            })
                        }
                    });
                }
                this.showCreateDialog = false;
                this.tweetDraft = ''
            }
        }
    }
</script>

<style scoped>

</style>