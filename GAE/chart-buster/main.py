#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2, json
from google.appengine.ext import ndb
from collections import OrderedDict


class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.set_status(500)
        self.response.write('Hello world!')

class MovieHandler(webapp2.RequestHandler):
    def post(self):
        movie = mMovie()
        movie.name = self.request.get('name')
        movie.url = self.request.get('movie_url')
        movie.put()

    def get(self):
        self.response.headers['Content-Type'] = 'application/json'
        json.dump(mMovie.get_list(), self.response.out)


class RateHandler(webapp2.RequestHandler):
    def post(self):
        movie_key = ndb.Key(urlsafe=self.request.get('movieid'))
        movie = movie_key.get()
        new_rating = int(self.request.get('rating'))
        if (new_rating>5):
            self.response.set_status(500)
            self.response.write('you aint thinking straight')
        else:
            movie.rating = new_rating
            movie.put()

    def handle_exception(self, exception, debug):
        self.response.set_status(500)
        self.response.write('stop being funny.')

class BookingHandler(webapp2.RequestHandler):
    def post(self):
        movie_key = ndb.Key(urlsafe=self.request.get('movieid'))
        movie = movie_key.get()
        seats_to_book = int(self.request.get('seats'))
        if (seats_to_book > movie.seats):
            self.response.set_status(500)
            self.response.write('you aint thinking straight')
        else:   
            movie.seats = movie.seats - seats_to_book
            movie.put()
            self.response.write('all set for the movie, dont forget to bring popcorn')

    def handle_exception(self, exception, debug):
        print(str(exception))
        self.response.set_status(500)
        self.response.write('stop being funny.')

class mMovie(ndb.Model):
    name = ndb.StringProperty()
    url = ndb.StringProperty(indexed=False)
    rating = ndb.IntegerProperty(indexed=False, default=0)
    seats = ndb.IntegerProperty(indexed=False, default=100)
    price = ndb.IntegerProperty(indexed=False, default=250)

    def to_message(self):
        return OrderedDict([
            ("id", self.key.urlsafe()),
            ("name", self.name),
            ("url", self.url),
            ("rating", self.rating),
            ("seats", self.seats),
            ("price", self.price)
        ])

    @classmethod
    def get_list(self):
        query = mMovie.query()
        return [movie.to_message() for movie in query]

app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/movies', MovieHandler),
    ('/rate', RateHandler),
    ('/book', BookingHandler)
], debug=True)
