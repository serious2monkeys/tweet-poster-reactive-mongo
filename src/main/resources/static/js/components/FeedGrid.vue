<template>
    <div>
        <FeedMessage :deleteTweet="deleteTweet"
                     :key="tweet.id"
                     :tweet="tweet"
                     v-for="tweet in sortedTweets"/>
    </div>
</template>

<script>
    import FeedMessage from "./FeedMessage.vue";

    export default {
        name: "FeedGrid",
        components: {FeedMessage},
        props: ['tweets'],
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
            }
        }
    }
</script>

<style scoped>

</style>