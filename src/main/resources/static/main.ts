import { WebSocketLink } from "@apollo/client/link/ws";
import { SubscriptionClient } from 'subscriptions-transport-ws';
import { ApolloClient, InMemoryCache } from "@apollo/client";
//import { useSubscription } from '@apollo/react-hooks';
import gql from 'graphql-tag';
import * as $ from 'jquery';

const GRAPHQL_ENDPOINT = 'ws://localhost:8080/subscriptions';

const client = new SubscriptionClient(GRAPHQL_ENDPOINT, {
  reconnect: true,
});

const link = new WebSocketLink(client);

const apolloClient = new ApolloClient({
  link: link,
  cache: new InMemoryCache()
});

const asGql = gql`
subscription BookSubscription {
  subscribeBooks {
    id,
    name
  }
}
`

const s = apolloClient.subscribe({
  query: asGql
})
s.subscribe({
  next: ({ data }) => {
    const result = document.getElementById("result");
    $("#result").append(JSON.stringify(data));
    $("#result").append("<br>");
  }
});


