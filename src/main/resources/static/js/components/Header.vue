<template>
    <v-app-bar>
        <v-app-bar-nav-icon></v-app-bar-nav-icon>
        <v-toolbar-title>
            Brand-new TweetApp
            <v-btn v-if="hasUser" href="/logout" icon>
                <v-icon>exit_to_app</v-icon>
            </v-btn>
        </v-toolbar-title>
    </v-app-bar>
</template>

<script>
    export default {
        name: "Header",
        computed:  {
            hasUser() {
                return authenticated;
            }
        }
    }
</script>

<style scoped>

</style>